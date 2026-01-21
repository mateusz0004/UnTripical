import { useState } from 'react';
import { Alert, Box, Button, Card, CardContent, Stack, TextField, Typography } from '@mui/material';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { useNavigate, Link as RouterLink } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import { getErrorMessage } from '../api/http';

const schema = z.object({
  email: z.string().email('Podaj poprawny email'),
  username: z.string().min(3, 'Minimum 3 znaki'),
  password: z.string().min(4, 'Minimum 4 znaki'),
});

type FormValues = z.infer<typeof schema>;

export function RegisterPage() {
  const { register: registerUser } = useAuth();
  const nav = useNavigate();
  const [error, setError] = useState<string | null>(null);

  const {
    register,
    handleSubmit,
    formState: { isSubmitting, errors },
  } = useForm<FormValues>({ resolver: zodResolver(schema) });

  const onSubmit = async (values: FormValues) => {
    setError(null);
    try {
      await registerUser(values);
      nav('/');
    } catch (e) {
      setError(getErrorMessage(e));
    }
  };

  return (
    <Box sx={{ maxWidth: 560, mx: 'auto' }}>
      <Card>
        <CardContent>
          <Typography variant="h5" sx={{ fontWeight: 900, mb: 2 }}>
            Rejestracja
          </Typography>
          <Stack component="form" spacing={2} onSubmit={handleSubmit(onSubmit)}>
            {error && <Alert severity="error">{error}</Alert>}

            <TextField
              label="Email"
              autoComplete="email"
              error={!!errors.email}
              helperText={errors.email?.message}
              {...register('email')}
            />
            <TextField
              label="Nazwa użytkownika"
              autoComplete="username"
              error={!!errors.username}
              helperText={errors.username?.message}
              {...register('username')}
            />
            <TextField
              label="Hasło"
              type="password"
              autoComplete="new-password"
              error={!!errors.password}
              helperText={errors.password?.message}
              {...register('password')}
            />

            <Button type="submit" variant="contained" disabled={isSubmitting}>
              Załóż konto
            </Button>

            <Typography variant="body2" color="text.secondary">
              Masz już konto?{' '}
              <Button component={RouterLink} to="/login" size="small">
                Zaloguj się
              </Button>
            </Typography>

            <Typography variant="body2" color="text.secondary">
              Chcesz zostać przewodnikiem?{' '}
              <Button component={RouterLink} to="/guide-register" size="small">
                Rejestracja przewodnika
              </Button>
            </Typography>
          </Stack>
        </CardContent>
      </Card>
    </Box>
  );
}
