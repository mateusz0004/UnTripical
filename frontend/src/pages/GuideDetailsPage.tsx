import { useEffect, useMemo, useState } from 'react';
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  Chip,
  CircularProgress,
  Divider,
  Rating,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import { Link as RouterLink, useParams } from 'react-router-dom';
import { Controller, useForm } from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { addGuideReview, deleteGuideReview, getAnnouncementById, getGuideById } from '../api/client';
import type { AnnouncementResponseDTO, GuideDetailsResponseDTO } from '../api/types';
import { getErrorMessage } from '../api/http';
import { useAuth } from '../auth/AuthContext';
import { formatOneDecimal } from '../utils/number';

const addReviewSchema = z.object({
  numberOfStars: z
    .number({ invalid_type_error: 'Wybierz ocenę' })
    .min(1, 'Minimalna ocena to 1')
    .max(5, 'Maksymalna ocena to 5'),
  description: z.string().min(2, 'Podaj opis (min 2 znaki)').max(1000, 'Opis jest za długi'),
});

type AddReviewForm = z.infer<typeof addReviewSchema>;

export function GuideDetailsPage() {
  const { id } = useParams();
  const guideId = Number(id);

  const { user } = useAuth();
  const isLoggedIn = !!user;

  const [item, setItem] = useState<GuideDetailsResponseDTO | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [info, setInfo] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [announcementById, setAnnouncementById] = useState<Record<number, AnnouncementResponseDTO>>({});

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

  const refresh = async () => {
    setLoading(true);
    setError(null);
    try {
      const g = await getGuideById(guideId);
      setItem(g);
    } catch (e) {
      setError(getErrorMessage(e));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!Number.isFinite(guideId) || guideId <= 0) {
      setError('Niepoprawne id przewodnika');
      setLoading(false);
      return;
    }

    void refresh();
  }, [guideId]);

  useEffect(() => {
    const ids = (item?.guideAnnouncements ?? []).map((x) => x.announcementId).filter((x) => Number.isFinite(x));
    const unique = Array.from(new Set(ids)).slice(0, 20);
    if (!unique.length) {
      setAnnouncementById({});
      return;
    }

    let cancelled = false;
    (async () => {
      const results = await Promise.allSettled(unique.map((id) => getAnnouncementById(id)));
      if (cancelled) return;
      const next: Record<number, AnnouncementResponseDTO> = {};
      for (const r of results) {
        if (r.status === 'fulfilled') next[r.value.id] = r.value;
      }
      setAnnouncementById(next);
    })();

    return () => {
      cancelled = true;
    };
  }, [item?.guideAnnouncements]);

  const visibleAnnouncements = useMemo(() => {
    return (item?.guideAnnouncements ?? []).slice(0, 10);
  }, [item?.guideAnnouncements]);

  const onAddReview = async (values: AddReviewForm) => {
    setError(null);
    setInfo(null);
    try {
      await addGuideReview({
        guideId,
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

  const canDeleteReview = (reviewAuthorUsername: string): boolean => {
    if (!user) return false;
    if (user.userRole === 'ADMIN') return true;
    return user.username === reviewAuthorUsername;
  };

  const onDeleteGuideReview = async (orderIndex: number, reviewAuthorUsername: string) => {
    if (!canDeleteReview(reviewAuthorUsername)) return;
    const ok = window.confirm('Usunąć tę opinię?');
    if (!ok) return;

    setError(null);
    setInfo(null);
    try {
      await deleteGuideReview(guideId, orderIndex);
      setInfo('Usunięto opinię.');
      await refresh();
    } catch (e) {
      setError(getErrorMessage(e));
    }
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error) return <Alert severity="error">{error}</Alert>;
  if (!item) return <Alert severity="warning">Nie znaleziono przewodnika.</Alert>;

  return (
    <Box>
      <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 2 }}>
        <Typography variant="h5" sx={{ fontWeight: 900 }}>
          {item.username}
        </Typography>
        <Button component={RouterLink} to="/guides" size="small">
          Wróć do listy
        </Button>
      </Stack>

      <Card>
        <CardContent>
          {info && (
            <Alert severity="success" sx={{ mb: 2 }}>
              {info}
            </Alert>
          )}

          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1} sx={{ mb: 1 }}>
            <Chip label={item.specialisation} />
            <Chip label={item.experienceLevel} />
            <Chip label={item.closestBigCity} />
          </Stack>
          <Typography color="text.secondary">
            Telefon: {item.phoneNumber}
          </Typography>
          <Typography sx={{ mt: 1 }}>
            Ocena: {formatOneDecimal(item.avgRating)}/5 • Liczba ogłoszeń: {item.numberOfAnnouncements}
          </Typography>

          <Divider sx={{ my: 2 }} />

          <Typography variant="subtitle1" sx={{ fontWeight: 800, mb: 1 }}>
            Dodaj opinię
          </Typography>

          {isLoggedIn ? (
            <Stack component="form" spacing={2} onSubmit={handleSubmit(onAddReview)} sx={{ mb: 2 }}>
              <Controller
                control={control}
                name="numberOfStars"
                render={({ field }) => (
                  <Stack spacing={0.5}>
                    <Typography variant="body2" color="text.secondary">
                      Ocena
                    </Typography>
                    <Rating value={field.value ?? 0} onChange={(_, v) => field.onChange(v ?? 0)} />
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
          ) : (
            <Alert severity="info" sx={{ mb: 2 }}>
              Zaloguj się, aby dodać opinię o przewodniku.
            </Alert>
          )}

          <Typography variant="subtitle1" sx={{ fontWeight: 800, mb: 1 }}>
            Ogłoszenia
          </Typography>
          {visibleAnnouncements.length ? (
            <Stack spacing={1}>
              {visibleAnnouncements.map((a) => (
                <Card key={a.id} variant="outlined">
                  <CardContent>
                    <Typography sx={{ fontWeight: 800 }}>
                      {announcementById[a.announcementId]?.nameOfJourney ?? 'Ładowanie nazwy…'}
                    </Typography>
                    <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5 }}>
                      Przewodnik: {item.username}
                    </Typography>
                    <Button component={RouterLink} to={`/offers/${a.announcementId}`} size="small" sx={{ mt: 1 }}>
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
                            onClick={() => onDeleteGuideReview(r.orderIndex, r.usernameWhoWroteReview)}
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
