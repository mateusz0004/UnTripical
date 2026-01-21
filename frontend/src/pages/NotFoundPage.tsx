import { Alert, Button, Stack } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';

export function NotFoundPage() {
  return (
    <Stack spacing={2}>
      <Alert severity="warning">Nie znaleziono strony.</Alert>
      <Button component={RouterLink} to="/" variant="contained" sx={{ alignSelf: 'flex-start' }}>
        Wróć na stronę główną
      </Button>
    </Stack>
  );
}
