import { useEffect, useMemo, useState } from 'react';
import {
  Alert,
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Box,
  Button,
  Card,
  CardContent,
  Chip,
  CircularProgress,
  Divider,
  FormControl,
  InputLabel,
  MenuItem,
  Rating,
  Select,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import { Link as RouterLink, useParams } from 'react-router-dom';
import { Controller, useForm } from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import {
  addPlaceReview,
  createAnnouncement,
  deletePlaceReview,
  getPlaceById,
  getRegions,
  updatePlace,
} from '../api/client';
import type { AnnouncementRequestDTO, PlaceResponseDTO, PlaceType, PlaceUpdateDTO, RegionResponseDTO } from '../api/types';
import { getErrorMessage } from '../api/http';
import { useAuth } from '../auth/AuthContext';
import { ddMmYyyyToTimestamp } from '../utils/date';

type AnnouncementSortMode = 'DEFAULT' | 'PRICE_ASC' | 'PRICE_DESC' | 'DATE_ASC' | 'DATE_DESC';

const addReviewSchema = z.object({
  numberOfStars: z
    .number({ invalid_type_error: 'Wybierz ocenę' })
    .min(1, 'Minimalna ocena to 1')
    .max(5, 'Maksymalna ocena to 5'),
  description: z.string().min(2, 'Podaj opis (min 2 znaki)').max(1000, 'Opis jest za długi'),
});

type AddReviewForm = z.infer<typeof addReviewSchema>;

const addAnnouncementSchema = z.object({
  nameOfJourney: z.string().min(2, 'Podaj nazwę (min 2 znaki)'),
  description: z.string().min(2, 'Podaj opis (min 2 znaki)').max(2000, 'Opis jest za długi'),
  announcementType: z.enum(['GROUP', 'INDIVIDUAL'], { message: 'Wybierz typ ogłoszenia' }),
  date: z.string().min(10, 'Podaj datę'), // yyyy-mm-dd from input[type=date]
  price: z.coerce.number().min(0, 'Cena nie może być ujemna'),
  locationInfo: z.string().min(2, 'Podaj lokalizację (min 2 znaki)'),
  maxParticipants: z.coerce.number().int().positive('Podaj liczbę uczestników'),
});

type AddAnnouncementForm = z.infer<typeof addAnnouncementSchema>;

const editPlaceSchema = z.object({
  name: z.string().min(2, 'Podaj nazwę (min 2 znaki)'),
  city: z.string().min(2, 'Podaj miasto (min 2 znaki)'),
  addressStreet: z.string().min(2, 'Podaj ulicę (min 2 znaki)'),
  addressNumber: z.string().min(1, 'Podaj numer'),
  postalCode: z.string().min(3, 'Podaj kod pocztowy'),
  placeType: z.enum(['TYPICAL', 'UNTYPICAL'], { message: 'Wybierz typ miejsca' }),
  photoUrl: z.string().min(3, 'Podaj link do zdjęcia'),
  regionId: z.coerce.number().int().positive('Wybierz region'),
});

type EditPlaceForm = z.infer<typeof editPlaceSchema>;

export function PlaceDetailsPage() {
  const { id } = useParams();
  const placeId = Number(id);

  const { user } = useAuth();
  const isGuide = user?.userRole === 'GUIDE';
  const isAdmin = user?.userRole === 'ADMIN';

  const [item, setItem] = useState<PlaceResponseDTO | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [info, setInfo] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [announcementSortMode, setAnnouncementSortMode] = useState<AnnouncementSortMode>('DEFAULT');
  const [regions, setRegions] = useState<RegionResponseDTO[]>([]);

  const {
    register,
    handleSubmit,
    formState: { errors: reviewErrors, isSubmitting: submittingReview },
    reset,
    control,
  } = useForm<AddReviewForm>({
    resolver: zodResolver(addReviewSchema),
    defaultValues: {
      numberOfStars: 5,
      description: '',
    },
  });

  const {
    register: registerAnnouncement,
    handleSubmit: handleSubmitAnnouncement,
    formState: { errors: announcementErrors, isSubmitting: submittingAnnouncement },
    reset: resetAnnouncement,
  } = useForm<AddAnnouncementForm>({
    resolver: zodResolver(addAnnouncementSchema),
    defaultValues: {
      announcementType: 'GROUP',
      date: '',
      price: 0,
      maxParticipants: 10,
    },
  });

  const {
    register: registerEditPlace,
    handleSubmit: handleSubmitEditPlace,
    formState: { errors: editPlaceErrors, isSubmitting: submittingEditPlace },
    reset: resetEditPlace,
    control: editPlaceControl,
  } = useForm<EditPlaceForm>({
    resolver: zodResolver(editPlaceSchema),
    defaultValues: {
      name: '',
      city: '',
      addressStreet: '',
      addressNumber: '',
      postalCode: '',
      placeType: 'TYPICAL',
      photoUrl: '',
      regionId: 0,
    },
  });

  const refresh = async () => {
    setLoading(true);
    setError(null);
    try {
      const p = await getPlaceById(placeId);
      setItem(p);
    } catch (e) {
      setError(getErrorMessage(e));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!Number.isFinite(placeId) || placeId <= 0) {
      setError('Niepoprawne id miejsca');
      setLoading(false);
      return;
    }

    void refresh();
  }, [placeId]);

  useEffect(() => {
    const loadRegions = async () => {
      try {
        const data = await getRegions();
        setRegions(data);
      } catch {
        // Regions are only needed for ADMIN edit form; keep details page usable even if this fails.
        setRegions([]);
      }
    };

    void loadRegions();
  }, []);

  useEffect(() => {
    if (!item) return;
    resetEditPlace({
      name: item.name ?? '',
      city: item.city ?? '',
      addressStreet: item.addressStreet ?? '',
      addressNumber: item.addressNumber ?? '',
      postalCode: item.postalCode ?? '',
      placeType: (item.placeType as PlaceType) ?? 'TYPICAL',
      photoUrl: item.photoUrl ?? '',
      regionId: item.regionId ?? 0,
    });
  }, [item, resetEditPlace]);

  const onAddReview = async (values: AddReviewForm) => {
    setError(null);
    setInfo(null);
    try {
      await addPlaceReview({
        placeId,
        numberOfStars: values.numberOfStars,
        description: values.description,
      });
      reset({ numberOfStars: 5, description: '' });
      setInfo('Dodano opinię.');
      await refresh();
    } catch (e) {
      setError(getErrorMessage(e));
    }
  };

  const toBackendDate = (yyyyMmDd: string): string => {
    const parts = yyyyMmDd.split('-');
    if (parts.length !== 3) return yyyyMmDd;
    const [y, m, d] = parts;
    return `${d}-${m}-${y}`;
  };

  const onAddAnnouncement = async (values: AddAnnouncementForm) => {
    setError(null);
    setInfo(null);
    try {
      const dto: AnnouncementRequestDTO = {
        nameOfJourney: values.nameOfJourney,
        description: values.description,
        announcementType: values.announcementType,
        date: toBackendDate(values.date),
        price: values.price,
        locationInfo: values.locationInfo,
        maxParticipants: values.maxParticipants,
        placeId,
      };

      await createAnnouncement(dto);
      resetAnnouncement({ announcementType: 'GROUP', date: '', price: 0, maxParticipants: 10 });
      setInfo('Dodano ogłoszenie.');
      await refresh();
    } catch (e) {
      setError(getErrorMessage(e));
    }
  };

  const canDeleteReview = (reviewAuthorUsername: string): boolean => {
    if (!user) return false;
    if (user.userRole === 'ADMIN') return true;
    return user.username === reviewAuthorUsername;
  };

  const onDeletePlaceReview = async (orderIndex: number, reviewAuthorUsername: string) => {
    if (!canDeleteReview(reviewAuthorUsername)) return;
    const ok = window.confirm('Usunąć tę opinię?');
    if (!ok) return;

    setError(null);
    setInfo(null);
    try {
      await deletePlaceReview(placeId, orderIndex);
      setInfo('Usunięto opinię.');
      await refresh();
    } catch (e) {
      setError(getErrorMessage(e));
    }
  };

  const onEditPlace = async (values: EditPlaceForm) => {
    setError(null);
    setInfo(null);
    try {
      const dto: PlaceUpdateDTO = {
        name: values.name,
        city: values.city,
        addressStreet: values.addressStreet,
        addressNumber: values.addressNumber,
        postalCode: values.postalCode,
        placeType: values.placeType,
        photoUrl: values.photoUrl,
        regionId: values.regionId,
      };
      await updatePlace(placeId, dto);
      setInfo('Zapisano zmiany miejsca.');
      await refresh();
    } catch (e) {
      setError(getErrorMessage(e));
    }
  };

  const sortedAnnouncements = useMemo(() => {
    const arr = (item?.announcements ?? []).slice();
    const getDateMs = (a: { date: string }): number => ddMmYyyyToTimestamp(a.date) ?? 0;

    switch (announcementSortMode) {
      case 'PRICE_ASC':
        arr.sort((a, b) => a.price - b.price);
        break;
      case 'PRICE_DESC':
        arr.sort((a, b) => b.price - a.price);
        break;
      case 'DATE_ASC':
        arr.sort((a, b) => getDateMs(a) - getDateMs(b));
        break;
      case 'DATE_DESC':
        arr.sort((a, b) => getDateMs(b) - getDateMs(a));
        break;
      case 'DEFAULT':
      default:
        break;
    }

    return arr;
  }, [item?.announcements, announcementSortMode]);

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error) return <Alert severity="error">{error}</Alert>;
  if (!item) return <Alert severity="warning">Nie znaleziono miejsca.</Alert>;

  return (
    <Box>
      <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 2 }}>
        <Typography variant="h5" sx={{ fontWeight: 900 }}>
          {item.name}
        </Typography>
        <Button component={RouterLink} to="/places" size="small">
          Wróć do listy
        </Button>
      </Stack>

      {info && (
        <Alert severity="success" sx={{ mb: 2 }}>
          {info}
        </Alert>
      )}

      <Card>
        <CardContent>
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1} sx={{ mb: 1 }}>
            <Chip label={item.placeType} />
            <Chip label={item.verificationStatus} />
            <Chip label={`Ogłoszeń: ${item.announcements?.length ?? 0}`} />
            <Chip label={`Opinie: ${item.reviews?.length ?? 0}`} />
          </Stack>

          <Typography color="text.secondary">
            {item.city}, {item.addressStreet} {item.addressNumber}
          </Typography>

          {isAdmin ? (
            <Accordion sx={{ mt: 2 }} disableGutters>
              <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                <Typography sx={{ fontWeight: 800 }}>Edytuj miejsce</Typography>
              </AccordionSummary>
              <AccordionDetails>
                <Stack component="form" spacing={2} onSubmit={handleSubmitEditPlace(onEditPlace)}>
                  <TextField
                    label="Nazwa"
                    error={!!editPlaceErrors.name}
                    helperText={editPlaceErrors.name?.message}
                    {...registerEditPlace('name')}
                  />
                  <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
                    <TextField
                      label="Miasto"
                      error={!!editPlaceErrors.city}
                      helperText={editPlaceErrors.city?.message}
                      {...registerEditPlace('city')}
                      sx={{ flex: 1 }}
                    />
                    <TextField
                      label="Kod pocztowy"
                      error={!!editPlaceErrors.postalCode}
                      helperText={editPlaceErrors.postalCode?.message}
                      {...registerEditPlace('postalCode')}
                      sx={{ flex: 1 }}
                    />
                  </Stack>

                  <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
                    <TextField
                      label="Ulica"
                      error={!!editPlaceErrors.addressStreet}
                      helperText={editPlaceErrors.addressStreet?.message}
                      {...registerEditPlace('addressStreet')}
                      sx={{ flex: 1 }}
                    />
                    <TextField
                      label="Numer"
                      error={!!editPlaceErrors.addressNumber}
                      helperText={editPlaceErrors.addressNumber?.message}
                      {...registerEditPlace('addressNumber')}
                      sx={{ width: { xs: '100%', sm: 160 } }}
                    />
                  </Stack>

                  <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
                    <FormControl size="small" sx={{ minWidth: 220 }} error={!!editPlaceErrors.placeType}>
                      <InputLabel id="edit-place-type">Typ miejsca</InputLabel>
                      <Controller
                        control={editPlaceControl}
                        name="placeType"
                        render={({ field }) => (
                          <Select
                            labelId="edit-place-type"
                            label="Typ miejsca"
                            value={field.value}
                            onChange={(e) => field.onChange(e.target.value)}
                          >
                            <MenuItem value="TYPICAL">TYPICAL</MenuItem>
                            <MenuItem value="UNTYPICAL">UNTYPICAL</MenuItem>
                          </Select>
                        )}
                      />
                      {editPlaceErrors.placeType ? (
                        <Typography variant="caption" color="error" sx={{ mt: 0.5 }}>
                          {editPlaceErrors.placeType.message}
                        </Typography>
                      ) : null}
                    </FormControl>

                    <FormControl size="small" sx={{ minWidth: 260 }} error={!!editPlaceErrors.regionId}>
                      <InputLabel id="edit-place-region">Region</InputLabel>
                      <Controller
                        control={editPlaceControl}
                        name="regionId"
                        render={({ field }) => (
                          <Select
                            labelId="edit-place-region"
                            label="Region"
                            value={field.value}
                            onChange={(e) => field.onChange(Number(e.target.value))}
                          >
                            {regions.map((r) => (
                              <MenuItem key={r.id} value={r.id}>
                                {r.type} — {r.closestBigCity}
                              </MenuItem>
                            ))}
                          </Select>
                        )}
                      />
                      {editPlaceErrors.regionId ? (
                        <Typography variant="caption" color="error" sx={{ mt: 0.5 }}>
                          {editPlaceErrors.regionId.message}
                        </Typography>
                      ) : null}
                    </FormControl>
                  </Stack>

                  <TextField
                    label="Link do zdjęcia"
                    error={!!editPlaceErrors.photoUrl}
                    helperText={editPlaceErrors.photoUrl?.message}
                    {...registerEditPlace('photoUrl')}
                  />

                  <Button type="submit" variant="contained" disabled={submittingEditPlace}>
                    Zapisz zmiany
                  </Button>
                </Stack>
              </AccordionDetails>
            </Accordion>
          ) : null}

          <Divider sx={{ my: 2 }} />

          <Typography variant="subtitle1" sx={{ fontWeight: 800, mb: 1 }}>
            Dodaj opinię
          </Typography>

          <Stack component="form" spacing={2} onSubmit={handleSubmit(onAddReview)} sx={{ mb: 2 }}>
            <Controller
              control={control}
              name="numberOfStars"
              render={({ field }) => (
                <Stack spacing={0.5}>
                  <Typography variant="body2" color="text.secondary">
                    Ocena
                  </Typography>
                  <Rating
                    value={field.value ?? 0}
                    onChange={(_, v) => field.onChange(v ?? 0)}
                  />
                  {reviewErrors.numberOfStars ? (
                    <Typography variant="caption" color="error">
                      {reviewErrors.numberOfStars.message}
                    </Typography>
                  ) : (
                    <Typography variant="caption" color="text.secondary">
                      Wybierz 1–5 gwiazdek.
                    </Typography>
                  )}
                </Stack>
              )}
            />

            <TextField
              label="Opis"
              multiline
              minRows={3}
              error={!!reviewErrors.description}
              helperText={reviewErrors.description?.message}
              {...register('description')}
            />

            <Button type="submit" variant="contained" disabled={submittingReview}>
              Dodaj opinię
            </Button>
          </Stack>

          <Typography variant="subtitle1" sx={{ fontWeight: 800, mb: 1 }}>
            Ogłoszenia
          </Typography>

          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ mb: 1 }}>
            <FormControl size="small" sx={{ minWidth: 220 }}>
              <InputLabel id="place-ann-sort">Sortowanie</InputLabel>
              <Select
                labelId="place-ann-sort"
                label="Sortowanie"
                value={announcementSortMode}
                onChange={(e) => setAnnouncementSortMode(e.target.value as AnnouncementSortMode)}
              >
                <MenuItem value="DEFAULT">Domyślne</MenuItem>
                <MenuItem value="PRICE_ASC">Cena: rosnąco</MenuItem>
                <MenuItem value="PRICE_DESC">Cena: malejąco</MenuItem>
                <MenuItem value="DATE_ASC">Data: najwcześniej</MenuItem>
                <MenuItem value="DATE_DESC">Data: najpóźniej</MenuItem>
              </Select>
            </FormControl>
          </Stack>

          {isGuide ? (
            <Accordion sx={{ mb: 2 }} disableGutters>
              <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                <Typography sx={{ fontWeight: 800 }}>Dodaj ogłoszenie</Typography>
              </AccordionSummary>
              <AccordionDetails>
                <Stack component="form" spacing={2} onSubmit={handleSubmitAnnouncement(onAddAnnouncement)}>
                  <TextField
                    label="Nazwa wycieczki"
                    error={!!announcementErrors.nameOfJourney}
                    helperText={announcementErrors.nameOfJourney?.message}
                    {...registerAnnouncement('nameOfJourney')}
                  />
                  <TextField
                    label="Opis"
                    multiline
                    minRows={3}
                    error={!!announcementErrors.description}
                    helperText={announcementErrors.description?.message}
                    {...registerAnnouncement('description')}
                  />
                  <TextField
                    label="Typ ogłoszenia"
                    select
                    SelectProps={{ native: true }}
                    error={!!announcementErrors.announcementType}
                    helperText={announcementErrors.announcementType?.message}
                    {...registerAnnouncement('announcementType')}
                  >
                    <option value="GROUP">Grupowe</option>
                    <option value="INDIVIDUAL">Indywidualne</option>
                  </TextField>

                  <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
                    <TextField
                      label="Data"
                      type="date"
                      InputLabelProps={{ shrink: true }}
                      error={!!announcementErrors.date}
                      helperText={announcementErrors.date?.message ?? 'Format: wybierz z kalendarza'}
                      {...registerAnnouncement('date')}
                      sx={{ flex: 1 }}
                    />
                    <TextField
                      label="Cena"
                      type="number"
                      inputProps={{ min: 0, step: 1 }}
                      error={!!announcementErrors.price}
                      helperText={announcementErrors.price?.message}
                      {...registerAnnouncement('price')}
                      sx={{ flex: 1 }}
                    />
                  </Stack>

                  <TextField
                    label="Lokalizacja (opis)"
                    error={!!announcementErrors.locationInfo}
                    helperText={announcementErrors.locationInfo?.message}
                    {...registerAnnouncement('locationInfo')}
                  />

                  <TextField
                    label="Maks. uczestników"
                    type="number"
                    inputProps={{ min: 1, step: 1 }}
                    error={!!announcementErrors.maxParticipants}
                    helperText={announcementErrors.maxParticipants?.message}
                    {...registerAnnouncement('maxParticipants')}
                  />

                  <Button type="submit" variant="contained" disabled={submittingAnnouncement}>
                    Dodaj ogłoszenie
                  </Button>
                </Stack>
              </AccordionDetails>
            </Accordion>
          ) : null}

          {sortedAnnouncements.length ? (
            <Stack spacing={1}>
              {sortedAnnouncements.slice(0, 10).map((a) => (
                <Card key={a.id} variant="outlined">
                  <CardContent>
                    <Typography sx={{ fontWeight: 800 }}>{a.nameOfJourney}</Typography>
                    <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5 }}>
                      Przewodnik: {a.guideUsername ?? 'Nieznany'}
                    </Typography>
                    <Button component={RouterLink} to={`/offers/${a.id}`} size="small" sx={{ mt: 1 }}>
                      Zobacz ofertę
                    </Button>
                  </CardContent>
                </Card>
              ))}
            </Stack>
          ) : (
            <Alert severity="info">Brak ogłoszeń.</Alert>
          )}

          <Divider sx={{ my: 2 }} />

          <Typography variant="subtitle1" sx={{ fontWeight: 800, mb: 1 }}>
            Opinie
          </Typography>
          {item.reviews?.length ? (
            <Stack spacing={1}>
              {item.reviews.slice(0, 10).map((r) => (
                <Card key={r.orderIndex} variant="outlined">
                  <CardContent>
                    <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 0.5 }}>
                      <Typography sx={{ fontWeight: 800 }}>{r.usernameWhoWroteReview}</Typography>
                      <Stack direction="row" spacing={1} alignItems="center">
                        <Chip size="small" label={`${r.numberOfStars}/5`} />
                        {canDeleteReview(r.usernameWhoWroteReview) ? (
                          <Button
                            size="small"
                            color="error"
                            onClick={() => onDeletePlaceReview(r.orderIndex, r.usernameWhoWroteReview)}
                          >
                            Usuń
                          </Button>
                        ) : null}
                      </Stack>
                    </Stack>
                    <Typography variant="body2" color="text.secondary">
                      {r.description}
                    </Typography>
                  </CardContent>
                </Card>
              ))}
            </Stack>
          ) : (
            <Alert severity="info">Brak opinii.</Alert>
          )}
        </CardContent>
      </Card>
    </Box>
  );
}
