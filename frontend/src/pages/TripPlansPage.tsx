import { useEffect, useMemo, useState } from 'react';
import {
  Alert,
  Autocomplete,
  Box,
  Button,
  Card,
  CardContent,
  Chip,
  Divider,
  Grid,
  IconButton,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import { Controller, useForm } from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import {
  createTripPlan,
  createTripStop,
  deleteTripPlan,
  deleteTripStop,
  getPlaceById,
  getPopularPlaces,
  getTripPlansAll,
  getTripStops,
  searchPlacesByName,
} from '../api/client';
import type { PlaceResponseDTO, TripPlanResponseDTO, TripStopResponseDTO } from '../api/types';
import { getErrorMessage } from '../api/http';
import { isoToDdMmYyyy } from '../utils/date';

const createPlanSchema = z.object({
  name: z.string().min(2, 'Podaj nazwę (min 2 znaki)'),
  startDateIso: z.string().min(10, 'Wybierz datę'),
});

type CreatePlanForm = z.infer<typeof createPlanSchema>;

const addStopSchema = z.object({
  placeId: z.coerce.number().int().positive('Wybierz miejsce z listy'),
  description: z.string().min(2, 'Podaj opis'),
});

type AddStopForm = z.infer<typeof addStopSchema>;

export function TripPlansPage() {
  const [plans, setPlans] = useState<TripPlanResponseDTO[]>([]);
  const [selected, setSelected] = useState<TripPlanResponseDTO | null>(null);
  const [stops, setStops] = useState<TripStopResponseDTO[]>([]);
  const [placesById, setPlacesById] = useState<Record<number, PlaceResponseDTO>>({});
  const [placeQuery, setPlaceQuery] = useState('');
  const [placeOptions, setPlaceOptions] = useState<PlaceResponseDTO[]>([]);
  const [selectedPlace, setSelectedPlace] = useState<PlaceResponseDTO | null>(null);
  const [loadingPlaceOptions, setLoadingPlaceOptions] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [info, setInfo] = useState<string | null>(null);
  const [loadingPlans, setLoadingPlans] = useState(false);
  const [loadingStops, setLoadingStops] = useState(false);

  const {
    register: regPlan,
    handleSubmit: submitPlan,
    formState: { errors: planErrors, isSubmitting: creatingPlan },
    reset: resetPlan,
  } = useForm<CreatePlanForm>({
    resolver: zodResolver(createPlanSchema),
    defaultValues: {
      startDateIso: new Date().toISOString().slice(0, 10),
    },
  });

  const {
    register: regStop,
    handleSubmit: submitStop,
    formState: { errors: stopErrors, isSubmitting: creatingStop },
    reset: resetStop,
    control: stopControl,
  } = useForm<AddStopForm>({
    resolver: zodResolver(addStopSchema),
  });

  const selectedId = selected?.id ?? null;
  const selectedMeta = useMemo(() => {
    if (!selected) return null;
    return {
      date: selected.dayWhenTripPlanIsStarting,
      totalDistance: selected.totalDistance,
      stopsCount: selected.tripStops?.length ?? stops.length,
    };
  }, [selected, stops.length]);

  const refreshPlans = async () => {
    setLoadingPlans(true);
    setError(null);
    try {
      const res = await getTripPlansAll();
      setPlans(res);
      if (selectedId) {
        const freshSelected = res.find((p) => p.id === selectedId) ?? null;
        setSelected(freshSelected);
      }
    } catch (e) {
      setError(getErrorMessage(e));
    } finally {
      setLoadingPlans(false);
    }
  };

  const refreshStops = async (tripPlanId: number) => {
    setLoadingStops(true);
    setError(null);
    try {
      const res = await getTripStops(tripPlanId);
      setStops(res);

      const ids = Array.from(new Set(res.map((s) => s.placeId))).filter((id) => Number.isFinite(id) && id > 0);
      if (!ids.length) {
        setPlacesById({});
      } else {
        const places = await Promise.all(ids.map((id) => getPlaceById(id).catch(() => null)));
        const map: Record<number, PlaceResponseDTO> = {};
        for (const p of places) {
          if (p) map[p.id] = p;
        }
        setPlacesById(map);
      }
    } catch (e) {
      setError(getErrorMessage(e));
    } finally {
      setLoadingStops(false);
    }
  };

  useEffect(() => {
    void refreshPlans();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    if (!selectedId) {
      setStops([]);
      setPlacesById({});
      return;
    }
    void refreshStops(selectedId);
  }, [selectedId]);

  useEffect(() => {
    const q = placeQuery.trim();
    const handle = window.setTimeout(() => {
      (async () => {
        setLoadingPlaceOptions(true);
        try {
          const res = q ? await searchPlacesByName(q) : await getPopularPlaces();
          const approved = res.filter((p) => p.verificationStatus === 'APPROVED');

          // Keep the currently selected place in the list so Autocomplete can render it.
          const merged = selectedPlace && !approved.some((p) => p.id === selectedPlace.id)
            ? [selectedPlace, ...approved]
            : approved;

          setPlaceOptions(merged);
        } catch {
          setPlaceOptions(selectedPlace ? [selectedPlace] : []);
        } finally {
          setLoadingPlaceOptions(false);
        }
      })();
    }, q ? 300 : 0);

    return () => window.clearTimeout(handle);
  }, [placeQuery, selectedPlace]);

  const onCreatePlan = async (values: CreatePlanForm) => {
    setError(null);
    setInfo(null);
    try {
      const dto = {
        name: values.name,
        dayWhenTripPlanIsStarting: isoToDdMmYyyy(values.startDateIso),
      };
      await createTripPlan(dto);
      resetPlan();
      setInfo('Utworzono plan.');
      await refreshPlans();
    } catch (e) {
      setError(getErrorMessage(e));
    }
  };

  const onDeletePlan = async (name: string) => {
    setError(null);
    setInfo(null);
    try {
      await deleteTripPlan(name);
      if (selected?.name === name) setSelected(null);
      setInfo('Usunięto plan.');
      await refreshPlans();
    } catch (e) {
      setError(getErrorMessage(e));
    }
  };

  const onAddStop = async (values: AddStopForm) => {
    if (!selectedId) return;
    setError(null);
    setInfo(null);
    try {
      await createTripStop({
        tripPlanId: selectedId,
        placeId: values.placeId,
        description: values.description,
      });
      resetStop();
      setPlaceQuery('');
      setSelectedPlace(null);
      setInfo('Dodano przystanek.');
      await refreshStops(selectedId);
      await refreshPlans();
    } catch (e) {
      setError(getErrorMessage(e));
    }
  };

  const onDeleteStop = async (orderIndex: number) => {
    if (!selectedId) return;
    setError(null);
    setInfo(null);
    try {
      await deleteTripStop(orderIndex, selectedId);
      setInfo('Usunięto przystanek.');
      await refreshStops(selectedId);
      await refreshPlans();
    } catch (e) {
      setError(getErrorMessage(e));
    }
  };

  return (
    <Box>
      <Typography variant="h5" sx={{ fontWeight: 900, mb: 1 }}>
        Moje plany
      </Typography>
      <Typography color="text.secondary" sx={{ mb: 2 }}>
        Twórz plan podróży i dodawaj przystanki.
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}
      {info && (
        <Alert severity="success" sx={{ mb: 2 }}>
          {info}
        </Alert>
      )}

      <Grid container spacing={2}>
        <Grid item xs={12} md={5}>
          <Card sx={{ mb: 2 }}>
            <CardContent>
              <Typography variant="h6" sx={{ fontWeight: 800, mb: 1 }}>
                Nowy plan
              </Typography>

              <Stack component="form" spacing={2} onSubmit={submitPlan(onCreatePlan)}>
                <TextField
                  label="Nazwa"
                  error={!!planErrors.name}
                  helperText={planErrors.name?.message}
                  {...regPlan('name')}
                />
                <TextField
                  label="Data startu"
                  type="date"
                  InputLabelProps={{ shrink: true }}
                  error={!!planErrors.startDateIso}
                  helperText={planErrors.startDateIso?.message}
                  {...regPlan('startDateIso')}
                />
                <Button type="submit" variant="contained" disabled={creatingPlan}>
                  Utwórz plan
                </Button>
              </Stack>
            </CardContent>
          </Card>

          <Card>
            <CardContent>
              <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 1 }}>
                <Typography variant="h6" sx={{ fontWeight: 800 }}>
                  Twoje plany
                </Typography>
                <Button size="small" onClick={() => void refreshPlans()} disabled={loadingPlans}>
                  Odśwież
                </Button>
              </Stack>

              <Stack spacing={1}>
                {plans.map((p) => (
                  <Card key={p.id} variant="outlined" sx={{ borderColor: selectedId === p.id ? 'primary.main' : 'divider' }}>
                    <CardContent>
                      <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 0.5 }}>
                        <Button
                          variant="text"
                          onClick={() => setSelected(p)}
                          sx={{ fontWeight: 800, textTransform: 'none', px: 0 }}
                        >
                          {p.name}
                        </Button>
                        <IconButton aria-label="delete" onClick={() => void onDeletePlan(p.name)} size="small">
                          <DeleteIcon fontSize="small" />
                        </IconButton>
                      </Stack>
                      <Stack direction="row" spacing={1} sx={{ flexWrap: 'wrap' }}>
                        <Chip size="small" label={p.dayWhenTripPlanIsStarting} />
                        <Chip size="small" label={`Dystans: ${p.totalDistance ?? 0}`} />
                      </Stack>
                    </CardContent>
                  </Card>
                ))}

                {!plans.length && <Alert severity="info">Brak planów. Utwórz pierwszy!</Alert>}
              </Stack>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={7}>
          <Card>
            <CardContent>
              <Typography variant="h6" sx={{ fontWeight: 800, mb: 1 }}>
                Szczegóły planu
              </Typography>

              {!selected ? (
                <Alert severity="info">Wybierz plan z listy po lewej.</Alert>
              ) : (
                <>
                  <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1} sx={{ mb: 1 }}>
                    <Chip label={selected.name} />
                    {selectedMeta && (
                      <>
                        <Chip label={`Start: ${selectedMeta.date}`} />
                        <Chip label={`Dystans: ${selectedMeta.totalDistance ?? 0}`} />
                        <Chip label={`Przystanki: ${selectedMeta.stopsCount}`} />
                      </>
                    )}
                  </Stack>

                  <Divider sx={{ my: 2 }} />

                  <Typography variant="subtitle1" sx={{ fontWeight: 800, mb: 1 }}>
                    Dodaj przystanek (TripStop)
                  </Typography>

                  <Stack component="form" spacing={2} onSubmit={submitStop(onAddStop)} sx={{ mb: 2 }}>
                    <Controller
                      control={stopControl}
                      name="placeId"
                      render={({ field }) => (
                        <Autocomplete
                          options={placeOptions}
                          loading={loadingPlaceOptions}
                          openOnFocus
                          value={selectedPlace}
                          onChange={(_, value) => {
                            const id = value?.id ?? 0;
                            field.onChange(id);
                            setSelectedPlace(value ?? null);
                          }}
                          onInputChange={(_, value) => {
                            setPlaceQuery(value);
                          }}
                          getOptionLabel={(p) => `${p.name}${p.city ? ` • ${p.city}` : ''}`}
                          isOptionEqualToValue={(a, b) => a.id === b.id}
                          renderInput={(params) => (
                            <TextField
                              {...params}
                              label="Miejsce"
                              placeholder="Zacznij wpisywać nazwę…"
                              error={!!stopErrors.placeId}
                              helperText={
                                stopErrors.placeId?.message ??
                                (placeQuery.trim()
                                  ? 'Wybierz miejsce z listy podpowiedzi.'
                                  : 'Możesz wybrać z listy albo wpisać nazwę, żeby wyszukać.')
                              }
                            />
                          )}
                        />
                      )}
                    />
                    <TextField
                      label="Opis"
                      error={!!stopErrors.description}
                      helperText={stopErrors.description?.message}
                      {...regStop('description')}
                    />
                    <Button type="submit" variant="contained" disabled={creatingStop}>
                      Dodaj
                    </Button>
                  </Stack>

                  <Typography variant="subtitle1" sx={{ fontWeight: 800, mb: 1 }}>
                    Przystanki
                  </Typography>

                  {loadingStops && <Alert severity="info">Ładowanie przystanków…</Alert>}

                  <Stack spacing={1}>
                    {stops.map((s) => (
                      <Card key={s.id} variant="outlined">
                        <CardContent>
                          <Stack direction="row" justifyContent="space-between" alignItems="center">
                            <Stack spacing={0.5}>
                              <Typography sx={{ fontWeight: 800 }}>{s.description}</Typography>
                              <Typography variant="body2" color="text.secondary">
                                {placesById[s.placeId]
                                  ? `Miejsce: ${placesById[s.placeId].name} • ${placesById[s.placeId].city}`
                                  : `Miejsce #${s.placeId}`} • Kolejność: {s.orderIndex}
                              </Typography>
                            </Stack>
                            <IconButton aria-label="delete" onClick={() => void onDeleteStop(s.orderIndex)} size="small">
                              <DeleteIcon fontSize="small" />
                            </IconButton>
                          </Stack>
                        </CardContent>
                      </Card>
                    ))}

                    {!stops.length && <Alert severity="info">Brak przystanków dla tego planu.</Alert>}
                  </Stack>
                </>
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
}
