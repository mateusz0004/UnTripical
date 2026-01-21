import { useEffect, useMemo, useState } from 'react';
import {
  Alert,
  Box,
  Card,
  CardActionArea,
  CardContent,
  Chip,
  FormControl,
  Grid,
  InputLabel,
  MenuItem,
  Select,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { getAnnouncements } from '../api/client';
import type { AnnouncementResponseDTO, AnnouncementType } from '../api/types';
import { getErrorMessage } from '../api/http';
import { ddMmYyyyToTimestamp } from '../utils/date';

type SortMode = 'DEFAULT' | 'PRICE_ASC' | 'PRICE_DESC' | 'DATE_ASC' | 'DATE_DESC';

export function OffersPage() {
  const nav = useNavigate();
  const [items, setItems] = useState<AnnouncementResponseDTO[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [q, setQ] = useState('');
  const [type, setType] = useState<AnnouncementType | 'ALL'>('ALL');
  const [sortMode, setSortMode] = useState<SortMode>('DEFAULT');

  useEffect(() => {
    (async () => {
      try {
        const res = await getAnnouncements();
        setItems(res);
      } catch (e) {
        setError(getErrorMessage(e));
      }
    })();
  }, []);

  const filtered = useMemo(() => {
    const query = q.trim().toLowerCase();
    return items.filter((a) => {
      if (type !== 'ALL' && a.announcementType !== type) return false;
      if (!query) return true;
      return (
        a.nameOfJourney.toLowerCase().includes(query) ||
        a.description.toLowerCase().includes(query) ||
        a.locationInfo.toLowerCase().includes(query)
      );
    });
  }, [items, q, type]);

  const visibleItems = useMemo(() => {
    const arr = filtered.slice();

    const getDateMs = (a: AnnouncementResponseDTO): number => ddMmYyyyToTimestamp(a.date) ?? 0;

    switch (sortMode) {
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
  }, [filtered, sortMode]);

  return (
    <Box>
      <Typography variant="h5" sx={{ fontWeight: 900, mb: 1 }}>
        Oferty przewodników
      </Typography>
      <Typography color="text.secondary" sx={{ mb: 2 }}>
        Przeglądaj dostępne oferty i wyszukuj po nazwie, opisie lub lokalizacji.
      </Typography>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ mb: 2 }}>
        <TextField label="Szukaj" value={q} onChange={(e) => setQ(e.target.value)} fullWidth />
        <Stack direction="row" spacing={1} alignItems="center">
          <Chip
            label="Wszystkie"
            color={type === 'ALL' ? 'primary' : 'default'}
            onClick={() => setType('ALL')}
          />
          <Chip
            label="GROUP"
            color={type === 'GROUP' ? 'primary' : 'default'}
            onClick={() => setType('GROUP')}
          />
          <Chip
            label="INDIVIDUAL"
            color={type === 'INDIVIDUAL' ? 'primary' : 'default'}
            onClick={() => setType('INDIVIDUAL')}
          />
        </Stack>
        <FormControl size="small" sx={{ minWidth: 220 }}>
          <InputLabel id="offers-sort">Sortowanie</InputLabel>
          <Select
            labelId="offers-sort"
            label="Sortowanie"
            value={sortMode}
            onChange={(e) => setSortMode(e.target.value as SortMode)}
          >
            <MenuItem value="DEFAULT">Domyślne</MenuItem>
            <MenuItem value="PRICE_ASC">Cena: rosnąco</MenuItem>
            <MenuItem value="PRICE_DESC">Cena: malejąco</MenuItem>
            <MenuItem value="DATE_ASC">Data: najwcześniej</MenuItem>
            <MenuItem value="DATE_DESC">Data: najpóźniej</MenuItem>
          </Select>
        </FormControl>
      </Stack>

      <Grid container spacing={2}>
        {visibleItems.map((a) => (
          <Grid key={a.id} item xs={12} md={6} lg={4}>
            <Card>
              <CardActionArea onClick={() => nav(`/offers/${a.id}`)}>
                <CardContent>
                  <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 1 }}>
                    <Typography variant="h6" sx={{ fontWeight: 800 }}>
                      {a.nameOfJourney}
                    </Typography>
                    <Chip size="small" label={a.announcementType} />
                  </Stack>
                  <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                    {a.locationInfo} • {a.date}
                  </Typography>
                  <Typography variant="body2" sx={{ mb: 1 }}>
                    {a.description.length > 160 ? a.description.slice(0, 160) + '…' : a.description}
                  </Typography>
                  <Stack direction="row" spacing={1}>
                    <Chip size="small" label={`${a.price} zł`} />
                    <Chip size="small" label={`max ${a.maxParticipants}`} />
                  </Stack>
                </CardContent>
              </CardActionArea>
            </Card>
          </Grid>
        ))}
      </Grid>
    </Box>
  );
}
