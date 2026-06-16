import { Alert, Box, Typography } from '@mui/material';

export function ComingSoonPage({ title }: { title: string }) {
  return (
    <Box>
      <Typography variant="h5" sx={{ fontWeight: 900, mb: 1 }}>
        {title}
      </Typography>
      <Alert severity="info">Ten widok jest jeszcze w budowie.</Alert>
    </Box>
  );
}
