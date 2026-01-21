import { useEffect, useMemo, useState } from 'react';
import {
  Alert,
  Box,
  Collapse,
  Card,
  CardContent,
  Chip,
  CircularProgress,
  Divider,
  MenuItem,
  Stack,
  TextField,
  Typography,
  Button,
} from '@mui/material';
import { Link as RouterLink, useParams } from 'react-router-dom';
import { getAnnouncementById, getGuideById, getPlaceById, updateAnnouncement } from '../api/client';
import type { AnnouncementResponseDTO, GuideDetailsResponseDTO, PlaceResponseDTO, AnnouncementType } from '../api/types';
import { getErrorMessage } from '../api/http';
import { useAuth } from '../auth/AuthContext';
import { formatOneDecimal } from '../utils/number';

export function OfferDetailsPage() {
  const { id } = useParams();
  const offerId = Number(id);

  const { user } = useAuth();

  const [offer, setOffer] = useState<AnnouncementResponseDTO | null>(null);
  const [place, setPlace] = useState<PlaceResponseDTO | null>(null);
  const [guide, setGuide] = useState<GuideDetailsResponseDTO | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  const [editOpen, setEditOpen] = useState(false);
  const [saveInfo, setSaveInfo] = useState<string | null>(null);
  const [saveError, setSaveError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);
  const [editDescription, setEditDescription] = useState('');
  const [editAnnouncementType, setEditAnnouncementType] = useState<AnnouncementType>('GROUP');
  const [editDate, setEditDate] = useState('');
  const [editPrice, setEditPrice] = useState('');
  const [editLocationInfo, setEditLocationInfo] = useState('');
  const [editMaxParticipants, setEditMaxParticipants] = useState('');

  const guideId = useMemo(() => {
    const t = offer?.guideAnnouncementTables?.[0];
    return t?.guideDetailsId ?? null;
  }, [offer]);

  useEffect(() => {
    if (!Number.isFinite(offerId) || offerId <= 0) {
      setError('Niepoprawne id oferty');
      setLoading(false);
      return;
    }

    (async () => {
      setLoading(true);
      setError(null);
      try {
        const a = await getAnnouncementById(offerId);
        setOffer(a);
        setEditDescription(a.description ?? '');
        setEditAnnouncementType(a.announcementType ?? 'GROUP');
        setEditDate(a.date ?? '');
        setEditPrice(String(a.price ?? ''));
        setEditLocationInfo(a.locationInfo ?? '');
        setEditMaxParticipants(String(a.maxParticipants ?? ''));
        const [p, g] = await Promise.all([
          getPlaceById(a.placeId).catch(() => null),
          a.guideAnnouncementTables?.[0]?.guideDetailsId
            ? getGuideById(a.guideAnnouncementTables[0].guideDetailsId).catch(() => null)
            : Promise.resolve(null),
        ]);
        setPlace(p);
        setGuide(g);
      } catch (e) {
        setError(getErrorMessage(e));
      } finally {
        setLoading(false);
      }
    })();
  }, [offerId]);

  const isOwnerGuide = useMemo(() => {
    if (!user || user.userRole !== 'GUIDE') return false;
    if (!guide) return false;
    return user.username === guide.username;
  }, [user, guide]);

  const onSaveEdit = async () => {
    if (!offer) return;
    setSaveInfo(null);
    setSaveError(null);
    setSaving(true);
    try {
      const max = Number(editMaxParticipants);
      const price = Number(editPrice);

      if (!Number.isFinite(max) || max <= 0) {
        setSaveError('Podaj poprawną liczbę uczestników (min. 1).');
        return;
      }

      if (!Number.isFinite(price) || price < 0) {
        setSaveError('Podaj poprawną cenę (min. 0).');
        return;
      }

      if (!/^\d{2}-\d{2}-\d{4}$/.test(editDate.trim())) {
        setSaveError('Data musi mieć format dd-MM-yyyy (np. 20-01-2026).');
        return;
      }

      const updated = await updateAnnouncement(offer.nameOfJourney, {
        description: editDescription,
        announcementType: editAnnouncementType,
        date: editDate.trim(),
        price,
        locationInfo: editLocationInfo,
        maxParticipants: max,
      });
      setOffer(updated);
      setSaveInfo('Zapisano zmiany.');
      setEditOpen(false);
    } catch (e) {
      setSaveError(getErrorMessage(e));
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return <Alert severity="error">{error}</Alert>;
  }

  if (!offer) {
    return <Alert severity="warning">Nie znaleziono oferty.</Alert>;
  }

  return (
    <Box>
      <Stack direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" alignItems={{ xs: 'flex-start', sm: 'center' }} sx={{ mb: 2 }}>
        <Typography variant="h5" sx={{ fontWeight: 900 }}>
          {offer.nameOfJourney}
        </Typography>
        <Stack direction="row" spacing={1}>
          <Chip label={offer.announcementType} />
          <Chip label={`${offer.price} zł`} />
          <Chip label={`max ${offer.maxParticipants}`} />
        </Stack>
      </Stack>

      {isOwnerGuide && (
        <Card sx={{ mb: 2 }}>
          <CardContent>
            <Stack direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" alignItems={{ xs: 'stretch', sm: 'center' }} spacing={1}>
              <Typography variant="h6" sx={{ fontWeight: 800 }}>
                Edycja ogłoszenia
              </Typography>
              <Button size="small" onClick={() => setEditOpen((v) => !v)}>
                {editOpen ? 'Zamknij' : 'Edytuj'}
              </Button>
            </Stack>

            <Collapse in={editOpen}>
              <Stack spacing={2} sx={{ mt: 2 }}>
                {saveError && <Alert severity="error">{saveError}</Alert>}
                {saveInfo && <Alert severity="success">{saveInfo}</Alert>}

                <TextField
                  label="Opis"
                  value={editDescription}
                  onChange={(e) => setEditDescription(e.target.value)}
                  fullWidth
                  multiline
                  minRows={3}
                />

                <TextField
                  select
                  label="Typ ogłoszenia"
                  value={editAnnouncementType}
                  onChange={(e) => setEditAnnouncementType(e.target.value as AnnouncementType)}
                  fullWidth
                >
                  <MenuItem value="GROUP">GROUP</MenuItem>
                  <MenuItem value="INDIVIDUAL">INDIVIDUAL</MenuItem>
                </TextField>

                <TextField
                  label="Data (dd-MM-yyyy)"
                  value={editDate}
                  onChange={(e) => setEditDate(e.target.value)}
                  fullWidth
                />

                <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
                  <TextField
                    label="Cena"
                    value={editPrice}
                    onChange={(e) => setEditPrice(e.target.value)}
                    fullWidth
                  />
                  <TextField
                    label="Max uczestników"
                    value={editMaxParticipants}
                    onChange={(e) => setEditMaxParticipants(e.target.value)}
                    fullWidth
                  />
                </Stack>

                <TextField
                  label="Lokalizacja"
                  value={editLocationInfo}
                  onChange={(e) => setEditLocationInfo(e.target.value)}
                  fullWidth
                />

                <Button variant="contained" onClick={() => void onSaveEdit()} disabled={saving}>
                  {saving ? 'Zapisuję…' : 'Zapisz zmiany'}
                </Button>
              </Stack>
            </Collapse>
          </CardContent>
        </Card>
      )}

      <Card sx={{ mb: 2 }}>
        <CardContent>
          <Typography color="text.secondary">{offer.locationInfo} • {offer.date}</Typography>
          <Typography sx={{ mt: 1 }}>{offer.description}</Typography>
          <Divider sx={{ my: 2 }} />
          <Typography variant="subtitle2" color="text.secondary">
            Informacje dodatkowe
          </Typography>
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1} sx={{ mt: 1 }}>
            <Chip
              size="small"
              label={place ? `Miejsce: ${place.name}` : `Miejsce: #${offer.placeId}`}
            />
            {guideId && <Chip size="small" label={guide ? `Przewodnik: ${guide.username}` : `Przewodnik: #${guideId}`} />}
          </Stack>
        </CardContent>
      </Card>

      {guide && (
        <Card sx={{ mb: 2 }}>
          <CardContent>
            <Stack direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" alignItems={{ xs: 'flex-start', sm: 'center' }}>
              <Typography variant="h6" sx={{ fontWeight: 800 }}>
                Przewodnik: {guide.username}
              </Typography>
              <Button component={RouterLink} to={`/guides/${guide.id}`} size="small">
                Zobacz profil
              </Button>
            </Stack>
            <Typography color="text.secondary">
              {guide.specialisation} • {guide.closestBigCity} • {guide.experienceLevel}
            </Typography>
            <Typography sx={{ mt: 1 }}>
              Średnia ocena: {formatOneDecimal(guide.avgRating)}/5 • Ogłoszeń: {guide.numberOfAnnouncements}
            </Typography>
          </CardContent>
        </Card>
      )}

      {place && (
        <Card>
          <CardContent>
            <Typography variant="h6" sx={{ fontWeight: 800 }}>
              Miejsce: {place.name}
            </Typography>
            <Typography color="text.secondary">
              {place.city}, {place.addressStreet} {place.addressNumber}
            </Typography>
            <Typography sx={{ mt: 1 }}>Typ: {place.placeType}</Typography>
            <Button component={RouterLink} to={`/places/${place.id}`} size="small" sx={{ mt: 1 }}>
              Zobacz szczegóły miejsca
            </Button>
          </CardContent>
        </Card>
      )}
    </Box>
  );
}
