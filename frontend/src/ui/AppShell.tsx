import { useMemo, useState } from 'react';
import { Link as RouterLink, Outlet } from 'react-router-dom';
import {
  AppBar,
  Box,
  Button,
  Container,
  Drawer,
  IconButton,
  List,
  ListItemButton,
  ListItemText,
  Toolbar,
  Typography,
  Link,
  Stack,
  Chip,
  useMediaQuery,
  useTheme,
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import { useAuth } from '../auth/AuthContext';

export function AppShell() {
  const { user, status, logout } = useAuth();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  const [drawerOpen, setDrawerOpen] = useState(false);

  const navItems = useMemo(
    () =>
      [
        { label: 'Oferty', to: '/offers', show: true },
        { label: 'Przewodnicy', to: '/guides', show: true },
        { label: 'Miejsca', to: '/places', show: true },
        { label: 'Do akceptacji', to: '/places/pending', show: status === 'authenticated' && user?.userRole === 'GUIDE' },
        { label: 'Moje plany', to: '/plans', show: status === 'authenticated' },
        { label: 'Regiony', to: '/regions', show: status === 'authenticated' },
      ].filter((x) => x.show),
    [status, user?.userRole],
  );

  return (
    <Box sx={{ minHeight: '100vh', bgcolor: 'background.default', display: 'flex', flexDirection: 'column' }}>
      <AppBar position="static" color="transparent" elevation={0}>
        <Toolbar sx={{ gap: 2, minHeight: 64 }}>
          {isMobile && (
            <IconButton aria-label="menu" onClick={() => setDrawerOpen(true)}>
              <MenuIcon />
            </IconButton>
          )}

          <Link component={RouterLink} to="/" underline="none" color="inherit">
            <Typography variant="h6" sx={{ fontWeight: 800 }}>
              Untripical
            </Typography>
          </Link>

          {!isMobile && (
            <Stack direction="row" spacing={1} sx={{ flexGrow: 1, overflowX: 'auto' }}>
              {navItems.map((item) => (
                <Button key={item.to} component={RouterLink} to={item.to} color="inherit">
                  {item.label}
                </Button>
              ))}
            </Stack>
          )}

          <Box sx={{ flexGrow: 1, display: { xs: 'block', md: 'none' } }} />

          {status === 'authenticated' && user ? (
            <Stack direction="row" spacing={1} alignItems="center">
              <Chip size="small" label={`${user.username} • ${user.userRole}`} />
              <Button variant="outlined" color="inherit" onClick={logout}>
                Wyloguj
              </Button>
            </Stack>
          ) : (
            <Stack direction="row" spacing={1}>
              <Button component={RouterLink} to="/login" color="inherit">
                Logowanie
              </Button>
              <Button component={RouterLink} to="/register" variant="contained">
                Rejestracja
              </Button>
            </Stack>
          )}
        </Toolbar>
      </AppBar>

      <Drawer anchor="left" open={drawerOpen} onClose={() => setDrawerOpen(false)}>
        <Box sx={{ width: 280 }} role="presentation" onClick={() => setDrawerOpen(false)}>
          <Box sx={{ px: 2, py: 2 }}>
            <Typography variant="h6" sx={{ fontWeight: 900 }}>
              Untripical
            </Typography>
            {status === 'authenticated' && user && (
              <Typography variant="body2" color="text.secondary">
                Zalogowano jako: {user.username}
              </Typography>
            )}
          </Box>
          <List>
            {navItems.map((item) => (
              <ListItemButton key={item.to} component={RouterLink} to={item.to}>
                <ListItemText primary={item.label} />
              </ListItemButton>
            ))}
            {status !== 'authenticated' && (
              <>
                <ListItemButton component={RouterLink} to="/login">
                  <ListItemText primary="Logowanie" />
                </ListItemButton>
                <ListItemButton component={RouterLink} to="/register">
                  <ListItemText primary="Rejestracja" />
                </ListItemButton>
              </>
            )}
          </List>
        </Box>
      </Drawer>

      <Container maxWidth={false} sx={{ flex: 1, py: 3, px: { xs: 2, sm: 3, md: 4 } }}>
        <Outlet />
      </Container>

      <Box component="footer" sx={{ py: 4, borderTop: '1px solid', borderColor: 'divider' }}>
        <Container maxWidth={false} sx={{ px: { xs: 2, sm: 3, md: 4 } }}>
          <Typography variant="body2" color="text.secondary">
            Untripical — aplikacja do planowania podróży i przeglądania ofert przewodników.
          </Typography>
        </Container>
      </Box>
    </Box>
  );
}
