import { useEffect, useMemo, useState } from 'react';
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  Divider,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { useNavigate } from 'react-router-dom';
import { getErrorMessage } from '../api/http';
import { getRegions, registerGuide } from '../api/client';
import type { ExperienceLevel, RegionResponseDTO, Specialisation } from '../api/types';

const schema = z.object({
  email: z.string().email('Podaj poprawny email'),
  username: z.string().min(3, 'Minimum 3 znaki'),
  password: z.string().min(4, 'Minimum 4 znaki'),
  phoneNumber: z.string().min(5, 'Podaj numer telefonu'),
  closestBigCity: z.string().min(2, 'Podaj miasto'),
  specialisation: z.string().min(1, 'Wybierz specjalizację'),
  experienceLevel: z.string().min(1, 'Wybierz poziom'),
  counterOfDidJourney: z.coerce.number().min(0),
  regionId: z.coerce.number().int().positive('Wybierz region'),
});

type FormValues = z.infer<typeof schema>;

const SPECIALISATIONS: Specialisation[] = [
  'HISTORY',
  'CULTURE',
  'NATURE',
  'MOUNTAIN_GUIDE',
  'CITY_TOUR',
  'FOOD_TOUR',
  'ADVENTURE',
  'ARCHITECTURE',
  'RELIGIOUS',
  'NIGHT_TOUR',
  'BIKE_TOUR',
  'WATER_TOUR',
  'LOCAL_LIFE',
];

const EXPERIENCE_LEVELS: ExperienceLevel[] = ['BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'EXPERT'];

export function GuideRegisterPage() {
  const nav = useNavigate();
  const [regions, setRegions] = useState<RegionResponseDTO[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loadingRegions, setLoadingRegions] = useState(false);

  const {
    register,
    handleSubmit,
    setValue,
    watch,
    formState: { isSubmitting, errors },
  } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: { counterOfDidJourney: 0 },
  });

  useEffect(() => {
    (async () => {
      setLoadingRegions(true);
      try {
        const res = await getRegions();
        setRegions(res);
      } catch (e) {
        setError(getErrorMessage(e));
      } finally {
        setLoadingRegions(false);
      }
    })();
  }, []);

  const regionOptions = useMemo(() => regions.slice().sort((a, b) => a.closestBigCity.localeCompare(b.closestBigCity)), [regions]);

  const onSubmit = async (values: FormValues) => {
    setError(null);
    try {
      await registerGuide({
        registerRequest: {
          email: values.email,
          username: values.username,
          password: values.password,
        },
        guideDetailsRequestDTO: {
          phoneNumber: values.phoneNumber,
          closestBigCity: values.closestBigCity,
          specialisation: values.specialisation,
          experienceLevel: values.experienceLevel,
          counterOfDidJourney: values.counterOfDidJourney,
          regionId: values.regionId,
        },
      });
      nav('/login');
    } catch (e) {
      setError(getErrorMessage(e));
    }
  };

  return (
    <Box sx={{ maxWidth: 760, mx: 'auto' }}>
      <Card>
        <CardContent>
          <Typography variant="h5" sx={{ fontWeight: 900, mb: 1 }}>
            Rejestracja przewodnika
          </Typography>
          <Typography color="text.secondary" sx={{ mb: 2 }}>
            Utwórz konto GUIDE wraz z danymi przewodnika.
          </Typography>

          <Stack component="form" spacing={2} onSubmit={handleSubmit(onSubmit)}>
            {error && <Alert severity="error">{error}</Alert>}

            <Typography variant="subtitle1" sx={{ fontWeight: 800 }}>
              Konto
            </Typography>

            <TextField label="Email" error={!!errors.email} helperText={errors.email?.message} {...register('email')} />
            <TextField
              label="Nazwa użytkownika"
              error={!!errors.username}
              helperText={errors.username?.message}
              {...register('username')}
            />
            <TextField
              label="Hasło"
              type="password"
              error={!!errors.password}
              helperText={errors.password?.message}
              {...register('password')}
            />

            <Divider />

            <Typography variant="subtitle1" sx={{ fontWeight: 800 }}>
              Dane przewodnika
            </Typography>

            <TextField
              label="Numer telefonu"
              error={!!errors.phoneNumber}
              helperText={errors.phoneNumber?.message}
              {...register('phoneNumber')}
            />
            <TextField
              label="Najbliższe duże miasto"
              error={!!errors.closestBigCity}
              helperText={errors.closestBigCity?.message}
              {...register('closestBigCity')}
            />

            <FormControl fullWidth error={!!errors.specialisation}>
              <InputLabel>Specjalizacja</InputLabel>
              <Select
                label="Specjalizacja"
                value={watch('specialisation') ?? ''}
                onChange={(e) => setValue('specialisation', e.target.value)}
              >
                {SPECIALISATIONS.map((s) => (
                  <MenuItem key={s} value={s}>
                    {s}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>

            <FormControl fullWidth error={!!errors.experienceLevel}>
              <InputLabel>Poziom doświadczenia</InputLabel>
              <Select
                label="Poziom doświadczenia"
                value={watch('experienceLevel') ?? ''}
                onChange={(e) => setValue('experienceLevel', e.target.value)}
              >
                {EXPERIENCE_LEVELS.map((l) => (
                  <MenuItem key={l} value={l}>
                    {l}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>

            <TextField
              label="Licznik odbytych wycieczek"
              type="number"
              error={!!errors.counterOfDidJourney}
              helperText={errors.counterOfDidJourney?.message}
              {...register('counterOfDidJourney')}
            />

            <FormControl fullWidth error={!!errors.regionId}>
              <InputLabel>Region</InputLabel>
              <Select
                label="Region"
                value={watch('regionId') ?? ''}
                onChange={(e) => setValue('regionId', Number(e.target.value))}
                disabled={loadingRegions}
              >
                {regionOptions.map((r) => (
                  <MenuItem key={r.id} value={r.id}>
                    {r.type} • {r.closestBigCity}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>

            {!loadingRegions && regionOptions.length === 0 && (
              <Alert severity="info">
                Brak dostępnych regionów. Dodaj region w zakładce „Regiony”, a następnie wróć do rejestracji.
              </Alert>
            )}

            <Button type="submit" variant="contained" disabled={isSubmitting}>
              Zarejestruj przewodnika
            </Button>
          </Stack>
        </CardContent>
      </Card>
    </Box>
  );
}
