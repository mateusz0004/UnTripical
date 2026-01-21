import { BrowserRouter, Route, Routes } from 'react-router-dom';
import { CssBaseline, ThemeProvider } from '@mui/material';
import { theme } from './ui/theme';
import { AuthProvider } from './auth/AuthContext';
import { AppShell } from './ui/AppShell';
import { ProtectedRoute } from './routes/ProtectedRoute';

import { HomePage } from './pages/HomePage';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
import { GuideRegisterPage } from './pages/GuideRegisterPage';
import { OffersPage } from './pages/OffersPage';
import { OfferDetailsPage } from './pages/OfferDetailsPage';
import { GuidesPage } from './pages/GuidesPage';
import { GuideDetailsPage } from './pages/GuideDetailsPage';
import { PlacesPage } from './pages/PlacesPage';
import { PlaceDetailsPage } from './pages/PlaceDetailsPage';
import { PlacesApprovalPage } from './pages/PlacesApprovalPage';
import { TripPlansPage } from './pages/TripPlansPage';
import { RegionsPage } from './pages/RegionsPage';
import { NotFoundPage } from './pages/NotFoundPage';

export default function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AuthProvider>
        <BrowserRouter>
          <Routes>
            <Route element={<AppShell />}>
              <Route index element={<HomePage />} />
              <Route path="login" element={<LoginPage />} />
              <Route path="register" element={<RegisterPage />} />
              <Route path="guide-register" element={<GuideRegisterPage />} />

              <Route element={<ProtectedRoute />}>
                <Route path="offers" element={<OffersPage />} />
                <Route path="offers/:id" element={<OfferDetailsPage />} />
                <Route path="guides" element={<GuidesPage />} />
                <Route path="guides/:id" element={<GuideDetailsPage />} />
                <Route path="places" element={<PlacesPage />} />
                <Route path="places/pending" element={<PlacesApprovalPage />} />
                <Route path="places/:id" element={<PlaceDetailsPage />} />
                <Route path="plans" element={<TripPlansPage />} />
                <Route path="regions" element={<RegionsPage />} />
              </Route>

              <Route path="*" element={<NotFoundPage />} />
            </Route>
          </Routes>
        </BrowserRouter>
      </AuthProvider>
    </ThemeProvider>
  );
}
