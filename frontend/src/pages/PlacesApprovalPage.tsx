import { useEffect, useMemo, useState } from 'react';
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  Chip,
  CircularProgress,
  Grid,
  Stack,
  Typography,
} from '@mui/material';
import { approvePlace, getPlacesWaitingForApproval, getRegions, rejectPlace } from '../api/client';
import type { PlaceResponseDTO, RegionResponseDTO } from '../api/types';
import { getErrorMessage } from '../api/http';
import { useAuth } from '../auth/AuthContext';

export function PlacesApprovalPage() {
  const { user } = useAuth();

  const [items, setItems] = useState<PlaceResponseDTO[]>([]);
  const [regions, setRegions] = useState<RegionResponseDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [info, setInfo] = useState<string | null>(null);
  const [actingId, setActingId] = useState<number | null>(null);

  const regionsById = useMemo(() => {
    const map = new Map<number, RegionResponseDTO>();
    for (const r of regions) map.set(r.id, r);
    return map;
  }, [regions]);

  const refresh = async () => {
    setLoading(true);
    setError(null);
    setInfo(null);
    try {
      const [pending, allRegions] = await Promise.all([getPlacesWaitingForApproval(), getRegions()]);
      setItems(pending);
      setRegions(allRegions);
    } catch (e) {
      setError(getErrorMessage(e));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void refresh();
  }, []);

  const onApprove = async (placeId: number) => {
    setActingId(placeId);
    setError(null);
    setInfo(null);
    try {
      await approvePlace(placeId);
      setItems((prev) => prev.filter((p) => p.id !== placeId));
      setInfo('Miejsce zatwierdzone. Powinno być teraz widoczne publicznie.');
    } catch (e) {
      setError(getErrorMessage(e));
    } finally {
      setActingId(null);
    }
  };

  const onReject = async (placeId: number) => {
    setActingId(placeId);
    setError(null);
    setInfo(null);
    try {
      await rejectPlace(placeId);
      setItems((prev) => prev.filter((p) => p.id !== placeId));
      setInfo('Miejsce odrzucone (REJECTED).');
    } catch (e) {
      setError(getErrorMessage(e));
    } finally {
      setActingId(null);
    }
  };

  if (!user || user.userRole !== 'GUIDE') {
    return (
      <Alert severity="warning">
        Ta sekcja jest dostępna tylko dla użytkowników z rolą GUIDE.
      </Alert>
    );
  }

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box>
      <Stack direction={{ xs: 'column', md: 'row' }} justifyContent="space-between" alignItems={{ xs: 'stretch', md: 'center' }} sx={{ mb: 2 }} spacing={1}>
        <Box>
          <Typography variant="h5" sx={{ fontWeight: 900, mb: 0.5 }}>
            Miejsca do zatwierdzenia
          </Typography>
          <Typography color="text.secondary">
            Zatwierdzaj miejsca o statusie WAITING_FOR_APPROVAL.
          </Typography>
        </Box>
        <Button variant="outlined" onClick={refresh} disabled={loading}>
          Odśwież
        </Button>
      </Stack>

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
        {items.map((p) => {
          const region = regionsById.get(p.regionId);
          const isWaiting = p.verificationStatus === 'WAITING_FOR_APPROVAL';
          const disabled = !isWaiting || actingId === p.id;

          return (
            <Grid key={p.id} item xs={12} md={6} lg={4}>
              <Card>
                <CardContent>
                  <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 1 }}>
                    <Typography variant="h6" sx={{ fontWeight: 800 }}>
                      {p.name}
                    </Typography>
                    <Chip size="small" label={p.placeType} />
                  </Stack>

                  <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                    {p.city}, {p.addressStreet} {p.addressNumber}
                  </Typography>

                  <Stack direction="row" spacing={1} sx={{ flexWrap: 'wrap', mb: 2 }}>
                    <Chip size="small" label={p.verificationStatus} />
                    {region && <Chip size="small" label={`Region: ${region.type} • ${region.closestBigCity}`} />}
                    <Chip size="small" label={`Dodane przez userId: ${p.userId}`} />
                  </Stack>

                  <Stack direction="row" spacing={1}>
                    <Button
                      variant="contained"
                      fullWidth
                      disabled={disabled}
                      onClick={() => void onApprove(p.id)}
                    >
                      {actingId === p.id ? 'Zapisywanie…' : 'Zatwierdź (APPROVED)'}
                    </Button>
                    <Button
                      variant="outlined"
                      color="error"
                      fullWidth
                      disabled={disabled}
                      onClick={() => void onReject(p.id)}
                    >
                      {actingId === p.id ? 'Zapisywanie…' : 'Odrzuć (REJECTED)'}
                    </Button>
                  </Stack>

                  {!isWaiting && (
                    <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mt: 1 }}>
                      To miejsce nie jest już w statusie WAITING_FOR_APPROVAL.
                    </Typography>
                  )}
                </CardContent>
              </Card>
            </Grid>
          );
        })}
      </Grid>

      {items.length === 0 && !error && (
        <Alert severity="info" sx={{ mt: 2 }}>
          Brak miejsc oczekujących na akceptację.
        </Alert>
      )}
    </Box>
  );
}
