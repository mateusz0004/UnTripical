export function isoToDdMmYyyy(isoDate: string): string {
  // isoDate: yyyy-MM-dd
  const [y, m, d] = isoDate.split('-');
  if (!y || !m || !d) return isoDate;
  return `${d}-${m}-${y}`;
}

export function ddMmYyyyToIso(ddmmyyyy: string): string {
  // ddmmyyyy: dd-MM-yyyy
  const [d, m, y] = ddmmyyyy.split('-');
  if (!y || !m || !d) return ddmmyyyy;
  return `${y}-${m}-${d}`;
}

export function ddMmYyyyToTimestamp(ddmmyyyy: string): number | null {
  const iso = ddMmYyyyToIso(ddmmyyyy);
  if (iso === ddmmyyyy) return null;
  const ms = Date.parse(iso);
  return Number.isFinite(ms) ? ms : null;
}
