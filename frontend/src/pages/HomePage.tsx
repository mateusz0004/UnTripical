import { Box, Button, Card, CardActions, CardContent, Grid, Typography } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

export function HomePage() {
  const { status } = useAuth();

  return (
    <Box>
      <Typography variant="h4" sx={{ fontWeight: 900, mb: 1 }}>
        Zaplanuj podróż i znajdź przewodnika
      </Typography>
      <Typography color="text.secondary" sx={{ mb: 3 }}>
        Przeglądaj oferty, wybieraj miejsca i twórz własne plany podróży.
      </Typography>

      <Grid container spacing={2}>
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="h6" sx={{ fontWeight: 800 }}>
                Oferty przewodników
              </Typography>
              <Typography color="text.secondary">
                Zobacz ogłoszenia i szczegóły wycieczek.
              </Typography>
            </CardContent>
            <CardActions>
              <Button component={RouterLink} to="/offers" size="small">
                Przeglądaj
              </Button>
            </CardActions>
          </Card>
        </Grid>
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="h6" sx={{ fontWeight: 800 }}>
                Przewodnicy
              </Typography>
              <Typography color="text.secondary">
                Filtruj po mieście i specjalizacji.
              </Typography>
            </CardContent>
            <CardActions>
              <Button component={RouterLink} to="/guides" size="small">
                Zobacz listę
              </Button>
            </CardActions>
          </Card>
        </Grid>
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="h6" sx={{ fontWeight: 800 }}>
                Plany podróży
              </Typography>
              <Typography color="text.secondary">
                Twórz plan i dodawaj przystanki.
              </Typography>
            </CardContent>
            <CardActions>
              <Button
                component={RouterLink}
                to={status === 'authenticated' ? '/plans' : '/login'}
                size="small"
                variant="contained"
              >
                {status === 'authenticated' ? 'Moje plany' : 'Zaloguj się'}
              </Button>
            </CardActions>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
}
