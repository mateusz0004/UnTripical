export function formatOneDecimal(value: number | null | undefined): string {
  const n = typeof value === 'number' ? value : Number(value);
  const safe = Number.isFinite(n) ? n : 0;
  // Always use dot as decimal separator (e.g., 4.7)
  return safe.toFixed(1);
}
