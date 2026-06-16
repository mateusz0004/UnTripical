import { useEffect, useMemo, useState } from 'react';
import {
  Alert,
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Box,
  Card,
  CardActionArea,
  CardContent,
  Chip,
  CircularProgress,
  Grid,
  Stack,
  TextField,
  Typography,
  Button,
  ToggleButton,
  ToggleButtonGroup,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { createPlace, getPopularPlaces, getRegions, searchPlacesByName } from '../api/client';
import type { PlaceRequestDTO, PlaceResponseDTO, PlaceType, RegionResponseDTO } from '../api/types';
import { getErrorMessage } from '../api/http';
import { formatOneDecimal } from '../utils/number';

const createPlaceSchema = z.object({
  name: z.string().min(2, 'Podaj nazwę (min 2 znaki)'),
  city: z.string().min(2, 'Podaj miasto'),
  addressStreet: z.string().min(2, 'Podaj ulicę'),
  addressNumber: z.string().min(1, 'Podaj numer'),
  postalCode: z.string().min(3, 'Podaj kod pocztowy'),
  placeType: z.enum(['TYPICAL', 'UNTYPICAL'], { message: 'Wybierz typ miejsca' }),
  photoUrl: z.string().min(3, 'Podaj link do zdjęcia'),
  regionId: z.coerce.number().int().positive('Wybierz region'),
});

type CreatePlaceForm = z.infer<typeof createPlaceSchema>;

export function PlacesPage() {
  const nav = useNavigate();
  const [items, setItems] = useState<PlaceResponseDTO[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [searching, setSearching] = useState(false);
  const [nameQuery, setNameQuery] = useState('');
  const [regions, setRegions] = useState<RegionResponseDTO[]>([]);
  const [loadingRegions, setLoadingRegions] = useState(false);
  const [createError, setCreateError] = useState<string | null>(null);
  const [createInfo, setCreateInfo] = useState<string | null>(null);
  const [placeTypeFilter, setPlaceTypeFilter] = useState<'ALL' | PlaceType>('ALL');
  const [sortMode, setSortMode] = useState<'DEFAULT' | 'RATING_DESC' | 'RATING_ASC'>('DEFAULT');

  const {
    register,
    handleSubmit,
    formState: { errors: formErrors, isSubmitting },
    reset,
  } = useForm<CreatePlaceForm>({
    resolver: zodResolver(createPlaceSchema),
    defaultValues: {
      placeType: 'TYPICAL',
    },
  });

  const regionsById = useMemo(() => {
    const map = new Map<number, RegionResponseDTO>();
    for (const r of regions) map.set(r.id, r);
    return map;
  }, [regions]);

  const filteredItems = useMemo(() => {
    if (placeTypeFilter === 'ALL') return items;
    return items.filter((p) => p.placeType === placeTypeFilter);
  }, [items, placeTypeFilter]);

  const placeTypeLabel = (t: PlaceType) => (t === 'TYPICAL' ? 'Typowe' : 'Nietypowe');

  const avgOpinion = (p: PlaceResponseDTO): number => {
    const reviews = (p.reviews ?? []).filter((r) => r !== null && r !== undefined);
    if (!reviews.length) return 0;
    const sum = reviews.reduce((acc, r) => acc + (Number(r.numberOfStars) || 0), 0);
    return sum / reviews.length;
  };

  const visibleItems = useMemo(() => {
    if (sortMode === 'DEFAULT') return filteredItems;
    const sorted = filteredItems.slice();
    sorted.sort((a, b) => {
      const ra = avgOpinion(a);
      const rb = avgOpinion(b);
      const diff = sortMode === 'RATING_DESC' ? rb - ra : ra - rb;
      if (diff !== 0) return diff;
      return a.name.localeCompare(b.name);
    });
    return sorted;
  }, [filteredItems, sortMode]);

  const refreshPlaces = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await getPopularPlaces();
      setItems(res);
    } catch (e) {
      setError(getErrorMessage(e));
    } finally {
      setLoading(false);
    }
  };

  const refreshPlacesByName = async (keyword: string) => {
    setSearching(true);
    setError(null);
    try {
      const res = await searchPlacesByName(keyword);
      setItems(res);
    } catch (e) {
      setError(getErrorMessage(e));
      setItems([]);
    } finally {
      setSearching(false);
    }
  };

  useEffect(() => {
    void refreshPlaces();
  }, []);

  useEffect(() => {
    const q = nameQuery.trim();
    const handle = setTimeout(() => {
      if (!q) {
        void refreshPlaces();
      } else {
        void refreshPlacesByName(q);
      }
    }, 300);
    return () => clearTimeout(handle);
  }, [nameQuery]);

  useEffect(() => {
    (async () => {
      setLoadingRegions(true);
      try {
        const res = await getRegions();
        setRegions(res);
      } catch {
        setRegions([]);
      } finally {
        setLoadingRegions(false);
      }
    })();
  }, []);

  const onCreatePlace = async (values: CreatePlaceForm) => {
    setCreateError(null);
    setCreateInfo(null);
    try {
      const dto: PlaceRequestDTO = {
        name: values.name,
        city: values.city,
        addressStreet: values.addressStreet,
        addressNumber: values.addressNumber,
        postalCode: values.postalCode,
        placeType: values.placeType,
        photoUrl: values.photoUrl,
        regionId: values.regionId,
      };

      await createPlace(dto);
      reset();
      setCreateInfo('Dodano miejsce. Teraz oczekuje na weryfikację, zanim będzie widoczne dla wszystkich.');
      await refreshPlaces();
    } catch (e) {
      setCreateError(getErrorMessage(e));
    }
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box>
      <Typography variant="h5" sx={{ fontWeight: 900, mb: 1 }}>
        Miejsca
      </Typography>
      <Typography color="text.secondary" sx={{ mb: 2 }}>
        Odkrywaj popularne miejsca i przejdź do szczegółów, aby zobaczyć więcej informacji.
      </Typography>

      <Accordion sx={{ mb: 2 }} disableGutters>
        <AccordionSummary expandIcon={<ExpandMoreIcon />}>
          <Typography sx={{ fontWeight: 800 }}>Dodaj nowe miejsce</Typography>
        </AccordionSummary>
        <AccordionDetails>
          <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
            Uzupełnij dane miejsca. Po dodaniu zostanie ono przekazane do weryfikacji.
          </Typography>

          {createError && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {createError}
            </Alert>
          )}
          {createInfo && (
            <Alert severity="success" sx={{ mb: 2 }}>
              {createInfo}
            </Alert>
          )}

          <Grid container spacing={2} component="form" onSubmit={handleSubmit(onCreatePlace)}>
            <Grid item xs={12} md={6}>
              <TextField
                label="Nazwa"
                fullWidth
                error={!!formErrors.name}
                helperText={formErrors.name?.message}
                {...register('name')}
              />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField
                label="Miasto"
                fullWidth
                error={!!formErrors.city}
                helperText={formErrors.city?.message}
                {...register('city')}
              />
            </Grid>

            <Grid item xs={12} md={6}>
              <TextField
                label="Ulica"
                fullWidth
                error={!!formErrors.addressStreet}
                helperText={formErrors.addressStreet?.message}
                {...register('addressStreet')}
              />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField
                label="Numer"
                fullWidth
                error={!!formErrors.addressNumber}
                helperText={formErrors.addressNumber?.message}
                {...register('addressNumber')}
              />
            </Grid>

            <Grid item xs={12} md={6}>
              <TextField
                label="Kod pocztowy"
                fullWidth
                error={!!formErrors.postalCode}
                helperText={formErrors.postalCode?.message}
                {...register('postalCode')}
              />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField
                label="Typ miejsca"
                select
                fullWidth
                SelectProps={{ native: true }}
                error={!!formErrors.placeType}
                helperText={formErrors.placeType?.message}
                {...register('placeType')}
              >
                <option value="TYPICAL">Typowe</option>
                <option value="UNTYPICAL">Nietypowe</option>
              </TextField>
            </Grid>

            <Grid item xs={12}>
              <TextField
                label="Link do zdjęcia"
                fullWidth
                placeholder="https://..."
                error={!!formErrors.photoUrl}
                helperText={formErrors.photoUrl?.message}
                {...register('photoUrl')}
              />
            </Grid>

            <Grid item xs={12} md={8}>
              <TextField
                label="Region"
                select
                fullWidth
                SelectProps={{ native: true }}
                disabled={loadingRegions || regions.length === 0}
                error={!!formErrors.regionId}
                helperText={
                  formErrors.regionId?.message ??
                  (loadingRegions ? 'Ładowanie regionów…' : regions.length ? 'Wybierz region z listy.' : 'Brak regionów do wyboru.')
                }
                {...register('regionId')}
              >
                <option value="">— wybierz —</option>
                {regions.map((r) => (
                  <option key={r.id} value={r.id}>
                    {r.type} • {r.closestBigCity}
                  </option>
                ))}
              </TextField>
            </Grid>
            <Grid item xs={12} md={4} sx={{ display: 'flex', alignItems: 'stretch' }}>
              <Button type="submit" variant="contained" fullWidth disabled={isSubmitting}>
                Dodaj miejsce
              </Button>
            </Grid>
          </Grid>
        </AccordionDetails>
      </Accordion>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      <Stack direction="row" spacing={1} alignItems="center" sx={{ mb: 2, flexWrap: 'wrap' }}>
        <Typography variant="body2" color="text.secondary">
          Filtr:
        </Typography>
        <ToggleButtonGroup
          size="small"
          exclusive
          value={placeTypeFilter}
          onChange={(_, next) => {
            if (next) setPlaceTypeFilter(next);
          }}
        >
          <ToggleButton value="ALL">Wszystkie</ToggleButton>
          <ToggleButton value="TYPICAL">Typowe</ToggleButton>
          <ToggleButton value="UNTYPICAL">Nietypowe</ToggleButton>
        </ToggleButtonGroup>
        <Typography variant="body2" color="text.secondary" sx={{ ml: 1 }}>
          ({visibleItems.length}/{items.length})
        </Typography>

        <TextField
          label="Szukaj po nazwie"
          size="small"
          value={nameQuery}
          onChange={(e) => setNameQuery(e.target.value)}
          sx={{ minWidth: 260 }}
          helperText={searching ? 'Szukam…' : ' '}
        />

        <Box sx={{ flex: 1 }} />

        <TextField
          label="Sortuj"
          select
          size="small"
          SelectProps={{ native: true }}
          value={sortMode}
          onChange={(e) => setSortMode(e.target.value as 'DEFAULT' | 'RATING_DESC' | 'RATING_ASC')}
          sx={{ minWidth: 240 }}
        >
          <option value="DEFAULT">Domyślnie</option>
          <option value="RATING_DESC">Średnia ocena: malejąco</option>
          <option value="RATING_ASC">Średnia ocena: rosnąco</option>
        </TextField>
      </Stack>

      <Grid container spacing={2}>
        {visibleItems.map((p) => (
          <Grid key={p.id} item xs={12} md={6} lg={4}>
            <Card>
              <CardActionArea onClick={() => nav(`/places/${p.id}`)}>
                <CardContent>
                  <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 1 }}>
                    <Typography variant="h6" sx={{ fontWeight: 800 }}>
                      {p.name}
                    </Typography>
                    <Chip size="small" label={placeTypeLabel(p.placeType)} />
                  </Stack>
                  <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                    {p.city}, {p.addressStreet} {p.addressNumber}
                  </Typography>
                  <Stack direction="row" spacing={1} sx={{ flexWrap: 'wrap' }}>
                    <Chip size="small" label={p.verificationStatus} />
                    <Chip size="small" label={`Ocena: ${formatOneDecimal(avgOpinion(p))}/5`} />
                    {regionsById.get(p.regionId) && (
                      <Chip size="small" label={`Region: ${regionsById.get(p.regionId)!.closestBigCity}`} />
                    )}
                    <Chip size="small" label={`Ogłoszeń: ${p.announcements?.length ?? 0}`} />
                    <Chip size="small" label={`Opinie: ${p.reviews?.length ?? 0}`} />
                  </Stack>
                </CardContent>
              </CardActionArea>
            </Card>
          </Grid>
        ))}
      </Grid>

      {items.length === 0 && !error && <Alert severity="info">Brak danych do wyświetlenia.</Alert>}
    </Box>
  );
}
