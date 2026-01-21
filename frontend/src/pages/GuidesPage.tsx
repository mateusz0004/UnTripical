import { useCallback, useEffect, useMemo, useState } from 'react';
import {
  Alert,
  Box,
  Button,
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
import type { GuideDetailsResponseDTO, Specialisation } from '../api/types';
import { getErrorMessage } from '../api/http';
import { getGuidesAll, getGuidesByCity, getGuidesBySpecialisation } from '../api/client';
import { formatOneDecimal } from '../utils/number';

type SortMode =
  | 'DEFAULT'
  | 'RATING_DESC'
  | 'RATING_ASC'
  | 'ANNOUNCEMENTS_DESC'
  | 'ANNOUNCEMENTS_ASC'
  | 'EXPERIENCE_DESC'
  | 'EXPERIENCE_ASC';

const EXPERIENCE_ORDER: Record<string, number> = {
  BEGINNER: 1,
  INTERMEDIATE: 2,
  ADVANCED: 3,
  EXPERT: 4,
};

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

export function GuidesPage() {
  const nav = useNavigate();

  const [items, setItems] = useState<GuideDetailsResponseDTO[]>([]);
  const [city, setCity] = useState('');
  const [spec, setSpec] = useState<Specialisation | 'ALL'>('ALL');
  const [sortMode, setSortMode] = useState<SortMode>('DEFAULT');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [hint, setHint] = useState<string>('Wybierz specjalizację albo wpisz miasto i kliknij Szukaj.');

  const canSearch = useMemo(() => spec !== 'ALL' || city.trim().length === 0 || city.trim().length >= 2, [spec, city]);

  const loadAllGuides = useCallback(async () => {
    setError(null);
    setHint('');
    setLoading(true);
    try {
      const res = await getGuidesAll();
      setItems(res);
      if (res.length === 0) setHint('Brak przewodników w bazie.');
    } catch (e) {
      setError(getErrorMessage(e));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void loadAllGuides();
  }, [loadAllGuides]);

  const visibleItems = useMemo(() => {
    if (sortMode === 'DEFAULT') return items;
    const sorted = items.slice();
    sorted.sort((a, b) => {
      const nameTieBreak = a.username.localeCompare(b.username);

      if (sortMode === 'RATING_ASC' || sortMode === 'RATING_DESC') {
        const ar = Number(a.avgRating ?? 0);
        const br = Number(b.avgRating ?? 0);
        const diff = sortMode === 'RATING_ASC' ? ar - br : br - ar;
        return diff !== 0 ? diff : nameTieBreak;
      }

      if (sortMode === 'ANNOUNCEMENTS_ASC' || sortMode === 'ANNOUNCEMENTS_DESC') {
        const an = Number(a.numberOfAnnouncements ?? 0);
        const bn = Number(b.numberOfAnnouncements ?? 0);
        const diff = sortMode === 'ANNOUNCEMENTS_ASC' ? an - bn : bn - an;
        return diff !== 0 ? diff : nameTieBreak;
      }

      if (sortMode === 'EXPERIENCE_ASC' || sortMode === 'EXPERIENCE_DESC') {
        const ae = EXPERIENCE_ORDER[String(a.experienceLevel)] ?? 0;
        const be = EXPERIENCE_ORDER[String(b.experienceLevel)] ?? 0;
        const diff = sortMode === 'EXPERIENCE_ASC' ? ae - be : be - ae;
        return diff !== 0 ? diff : nameTieBreak;
      }

      return nameTieBreak;
    });
    return sorted;
  }, [items, sortMode]);

  const onSearch = async () => {
    setError(null);
    setHint('');
    if (!canSearch) {
      setHint('Podaj miasto (min. 2 znaki) lub wybierz specjalizację.');
      return;
    }

    setLoading(true);
    try {
      const trimmedCity = city.trim();
      const res = spec !== 'ALL'
        ? await getGuidesBySpecialisation(spec)
        : trimmedCity.length >= 2
          ? await getGuidesByCity(trimmedCity)
          : await getGuidesAll();
      setItems(res);
      if (res.length === 0) setHint('Brak wyników. Zmień filtr i spróbuj ponownie.');
    } catch (e) {
      setError(getErrorMessage(e));
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box>
      <Typography variant="h5" sx={{ fontWeight: 900, mb: 1 }}>
        Przewodnicy
      </Typography>
      <Typography color="text.secondary" sx={{ mb: 2 }}>
        Wyszukiwanie po specjalizacji lub mieście.
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}
      {hint && (
        <Alert severity="info" sx={{ mb: 2 }}>
          {hint}
        </Alert>
      )}

      <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} sx={{ mb: 2 }}>
        <TextField
          label="Miasto"
          value={city}
          onChange={(e) => setCity(e.target.value)}
          fullWidth
        />

        <FormControl size="small" sx={{ minWidth: 220 }}>
          <InputLabel id="guides-sort-label">Sortowanie</InputLabel>
          <Select
            labelId="guides-sort-label"
            label="Sortowanie"
            value={sortMode}
            onChange={(e) => setSortMode(e.target.value as any)}
          >
            <MenuItem value="DEFAULT">Domyślne</MenuItem>
            <MenuItem value="RATING_DESC">Ocena: od najwyższej</MenuItem>
            <MenuItem value="RATING_ASC">Ocena: od najniższej</MenuItem>
            <MenuItem value="ANNOUNCEMENTS_DESC">Ilość ogłoszeń: malejąco</MenuItem>
            <MenuItem value="ANNOUNCEMENTS_ASC">Ilość ogłoszeń: rosnąco</MenuItem>
            <MenuItem value="EXPERIENCE_DESC">Doświadczenie: EXPERT → BEGINNER</MenuItem>
            <MenuItem value="EXPERIENCE_ASC">Doświadczenie: BEGINNER → EXPERT</MenuItem>
          </Select>
        </FormControl>

        <Stack direction="row" spacing={1} sx={{ flexWrap: 'wrap' }}>
          <Chip
            label="ALL"
            color={spec === 'ALL' ? 'primary' : 'default'}
            onClick={() => {
              setSpec('ALL');
              setCity('');
              void loadAllGuides();
            }}
          />
          {SPECIALISATIONS.map((s) => (
            <Chip
              key={s}
              label={s}
              color={spec === s ? 'primary' : 'default'}
              onClick={() => setSpec(s)}
            />
          ))}
        </Stack>
        <Button variant="contained" disabled={!canSearch || loading} onClick={onSearch} sx={{ minWidth: 140 }}>
          {loading ? 'Szukam…' : 'Szukaj'}
        </Button>
      </Stack>

      <Grid container spacing={2}>
        {visibleItems.map((g) => (
          <Grid key={g.id} item xs={12} md={6} lg={4}>
            <Card>
              <CardActionArea onClick={() => nav(`/guides/${g.id}`)}>
                <CardContent>
                  <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 1 }}>
                    <Typography variant="h6" sx={{ fontWeight: 800 }}>
                      {g.username}
                    </Typography>
                    <Chip size="small" label={g.specialisation} />
                  </Stack>
                  <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                    {g.closestBigCity} • {g.experienceLevel}
                  </Typography>
                  <Stack direction="row" spacing={1} sx={{ flexWrap: 'wrap' }}>
                    <Chip size="small" label={`Ocena: ${formatOneDecimal(g.avgRating)}/5`} />
                    <Chip size="small" label={`Ogłoszeń: ${g.numberOfAnnouncements}`} />
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
