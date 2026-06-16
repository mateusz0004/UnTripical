import { useState } from 'react';
import { Alert, Box, Button, Card, CardContent, Stack, TextField, Typography } from '@mui/material';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { useNavigate, Link as RouterLink } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import { getErrorMessage } from '../api/http';

const schema = z.object({
  username: z.string().min(1, 'Podaj nazwę użytkownika'),
  password: z.string().min(1, 'Podaj hasło'),
});

type FormValues = z.infer<typeof schema>;

export function LoginPage() {
  const { login } = useAuth();
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
      await login(values);
      nav('/');
    } catch (e) {
      setError(getErrorMessage(e));
    }
  };

  return (
    <Box sx={{ maxWidth: 520, mx: 'auto' }}>
      <Card>
        <CardContent>
          <Typography variant="h5" sx={{ fontWeight: 900, mb: 2 }}>
            Logowanie
          </Typography>
          <Stack component="form" spacing={2} onSubmit={handleSubmit(onSubmit)}>
            {error && <Alert severity="error">{error}</Alert>}

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
              autoComplete="current-password"
              error={!!errors.password}
              helperText={errors.password?.message}
              {...register('password')}
            />

            <Button type="submit" variant="contained" disabled={isSubmitting}>
              Zaloguj
            </Button>

            <Typography variant="body2" color="text.secondary">
              Nie masz konta?{' '}
              <Button component={RouterLink} to="/register" size="small">
                Zarejestruj się
              </Button>
            </Typography>
          </Stack>
        </CardContent>
      </Card>
    </Box>
  );
}
