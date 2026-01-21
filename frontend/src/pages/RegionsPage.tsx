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
import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { createRegion, deleteRegion, getRegions, updateRegion } from '../api/client';
import type { RegionRequestDTO, RegionResponseDTO, RegionType } from '../api/types';
import { getErrorMessage } from '../api/http';
import { useAuth } from '../auth/AuthContext';

const REGION_TYPES: RegionType[] = [
  'MOUNTAIN',
  'COASTAL',
  'VIEW_POINT',
  'LAKE',
  'FOREST',
  'DESERT',
  'HISTORICAL',
  'CULTURAL',
  'ADVENTURE',
  'RELIGIOUS',
  'ISLAND',
  'DISTRICT',
  'VOIVODESHIP',
  'PARK',
];

const regionSchema = z.object({
  type: z.custom<RegionType>((v) => typeof v === 'string' && REGION_TYPES.includes(v as RegionType), {
    message: 'Wybierz typ regionu',
  }),
  closestBigCity: z.string().min(2, 'Podaj miasto (min 2 znaki)'),
});

type RegionForm = z.infer<typeof regionSchema>;

export function RegionsPage() {
  const { user } = useAuth();
  const isAdmin = user?.userRole === 'ADMIN';

  const [regions, setRegions] = useState<RegionResponseDTO[]>([]);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [info, setInfo] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [busyDeleteId, setBusyDeleteId] = useState<number | null>(null);

  const {
    register,
    handleSubmit,
    setValue,
    reset,
    watch,
    formState: { errors, isSubmitting },
  } = useForm<RegionForm>({
    resolver: zodResolver(regionSchema),
    defaultValues: {
      type: 'VOIVODESHIP',
      closestBigCity: '',
    },
  });

  const watchedType = watch('type');

  const sortedRegions = useMemo(() => {
    return [...regions].sort((a, b) => a.closestBigCity.localeCompare(b.closestBigCity));
  }, [regions]);

  const refresh = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await getRegions();
      setRegions(res);
    } catch (e) {
      setError(getErrorMessage(e));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void refresh();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const beginEdit = (r: RegionResponseDTO) => {
    setInfo(null);
    setError(null);
    setEditingId(r.id);
    setValue('type', r.type);
    setValue('closestBigCity', r.closestBigCity);
  };

  const cancelEdit = () => {
    setEditingId(null);
    reset({ type: 'VOIVODESHIP', closestBigCity: '' });
  };

  const onSubmit = async (values: RegionForm) => {
    setError(null);
    setInfo(null);

    const dto: RegionRequestDTO = {
      type: values.type,
      closestBigCity: values.closestBigCity.trim(),
    };

    try {
      if (editingId) {
        await updateRegion(editingId, dto);
        setInfo('Zapisano zmiany regionu.');
      } else {
        await createRegion(dto);
        setInfo('Dodano region.');
      }
      cancelEdit();
      await refresh();
    } catch (e) {
      setError(getErrorMessage(e));
    }
  };

  const onDelete = async (regionId: number) => {
    setError(null);
    setInfo(null);

    if (!isAdmin) {
      setError('Brak uprawnień: usuwanie regionów jest dostępne tylko dla ADMIN.');
      return;
    }

    setBusyDeleteId(regionId);
    try {
      await deleteRegion(regionId);
      setInfo('Usunięto region.');
      await refresh();
    } catch (e) {
      setError(getErrorMessage(e));
    } finally {
      setBusyDeleteId(null);
    }
  };

  return (
    <Stack spacing={2}>
      <Box>
        <Typography variant="h4" sx={{ fontWeight: 800 }}>
          Regiony
        </Typography>
        <Typography variant="body2" color="text.secondary">
          Lista dostępnych regionów 
        </Typography>
      </Box>

      {error && <Alert severity="error">{error}</Alert>}
      {info && <Alert severity="success">{info}</Alert>}

      <Card variant="outlined">
        <CardContent>
          <Stack spacing={2} component="form" onSubmit={handleSubmit(onSubmit)}>
            <Typography variant="h6" sx={{ fontWeight: 700 }}>
              {editingId ? `Edycja regionu #${editingId}` : 'Dodaj region'}
            </Typography>

            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
              <FormControl fullWidth error={Boolean(errors.type)}>
                <InputLabel id="region-type">Typ</InputLabel>
                <Select
                  labelId="region-type"
                  label="Typ"
                  value={watchedType}
                  onChange={(e) => setValue('type', e.target.value as RegionType, { shouldValidate: true })}
                  disabled={isSubmitting}
                >
                  {REGION_TYPES.map((t) => (
                    <MenuItem key={t} value={t}>
                      {t}
                    </MenuItem>
                  ))}
                </Select>
                {errors.type?.message && (
                  <Typography variant="caption" color="error">
                    {String(errors.type.message)}
                  </Typography>
                )}
              </FormControl>

              <TextField
                label="Najbliższe duże miasto"
                fullWidth
                {...register('closestBigCity')}
                error={Boolean(errors.closestBigCity)}
                helperText={errors.closestBigCity?.message}
                disabled={isSubmitting}
              />
            </Stack>

            <Stack direction="row" spacing={1} justifyContent="flex-end">
              {editingId && (
                <Button onClick={cancelEdit} disabled={isSubmitting}>
                  Anuluj
                </Button>
              )}
              <Button type="submit" variant="contained" disabled={isSubmitting}>
                {editingId ? 'Zapisz' : 'Dodaj'}
              </Button>
            </Stack>
          </Stack>
        </CardContent>
      </Card>

      <Card variant="outlined">
        <CardContent>
          <Stack spacing={1}>
            <Stack direction="row" alignItems="center" justifyContent="space-between">
              <Typography variant="h6" sx={{ fontWeight: 700 }}>
                Lista ({sortedRegions.length})
              </Typography>
              <Button onClick={refresh} disabled={loading}>
                Odśwież
              </Button>
            </Stack>
            <Divider />

            {sortedRegions.length === 0 ? (
              <Typography variant="body2" color="text.secondary">
                Brak regionów.
              </Typography>
            ) : (
              <Stack spacing={1}>
                {sortedRegions.map((r) => (
                  <Box
                    key={r.id}
                    sx={{
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'space-between',
                      gap: 2,
                      p: 1.25,
                      border: '1px solid',
                      borderColor: 'divider',
                      borderRadius: 1,
                    }}
                  >
                    <Box>
                      <Typography sx={{ fontWeight: 700 }}>{r.closestBigCity}</Typography>
                      <Typography variant="body2" color="text.secondary">
                        #{r.id} • {r.type}
                      </Typography>
                    </Box>

                    <Stack direction="row" spacing={1}>
                      <Button
                        size="small"
                        startIcon={<EditIcon />}
                        onClick={() => beginEdit(r)}
                        disabled={isSubmitting}
                      >
                        Edytuj
                      </Button>
                      <Button
                        size="small"
                        color="error"
                        startIcon={<DeleteIcon />}
                        onClick={() => onDelete(r.id)}
                        disabled={!isAdmin || busyDeleteId === r.id}
                      >
                        Usuń
                      </Button>
                    </Stack>
                  </Box>
                ))}
              </Stack>
            )}
          </Stack>
        </CardContent>
      </Card>
    </Stack>
  );
}
