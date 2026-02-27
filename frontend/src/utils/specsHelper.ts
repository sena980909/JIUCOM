type Specs = Record<string, unknown>;

const CATEGORY_SPEC_KEYS: Record<string, { keys: string[]; format: (vals: (string | number)[]) => string }> = {
  CPU: {
    keys: ['cores', 'threads', 'socket', 'tdp'],
    format: ([cores, threads, socket, tdp]) =>
      [cores && `${cores}코어`, threads && `${threads}스레드`, socket, tdp && `${tdp}W`]
        .filter(Boolean)
        .join(' / '),
  },
  GPU: {
    keys: ['memory', 'memoryType', 'tdp'],
    format: ([memory, memoryType, tdp]) =>
      [memory && memoryType ? `${memory}GB ${memoryType}` : memory && `${memory}GB`, tdp && `${tdp}W`]
        .filter(Boolean)
        .join(' / '),
  },
  MOTHERBOARD: {
    keys: ['socket', 'chipset', 'formFactor'],
    format: (vals) => vals.filter(Boolean).join(' / '),
  },
  RAM: {
    keys: ['capacity', 'type', 'speed'],
    format: ([capacity, type, speed]) =>
      [capacity && `${capacity}GB`, type && speed ? `${type}-${speed}` : type]
        .filter(Boolean)
        .join(' / '),
  },
  SSD: {
    keys: ['capacity', 'interface'],
    format: ([capacity, iface]) =>
      [capacity && `${capacity}`, iface].filter(Boolean).join(' / '),
  },
  HDD: {
    keys: ['capacity', 'rpm'],
    format: ([capacity, rpm]) =>
      [capacity && `${capacity}`, rpm && `${rpm}RPM`].filter(Boolean).join(' / '),
  },
  POWER_SUPPLY: {
    keys: ['wattage', 'efficiency'],
    format: ([wattage, efficiency]) =>
      [wattage && `${wattage}W`, efficiency].filter(Boolean).join(' / '),
  },
  CASE: {
    keys: ['formFactor', 'type'],
    format: (vals) => vals.filter(Boolean).join(' / '),
  },
  COOLER: {
    keys: ['type', 'tdp'],
    format: ([type, tdp]) =>
      [type, tdp && `${tdp}W`].filter(Boolean).join(' / '),
  },
};

export function getSpecsSummary(category: string, specs: Specs | null | undefined): string {
  if (!specs || Object.keys(specs).length === 0) return '';

  const config = CATEGORY_SPEC_KEYS[category];
  if (!config) return '';

  const values = config.keys.map((key) => {
    const val = specs[key];
    if (val === null || val === undefined || val === '') return '';
    return String(val);
  });

  if (values.every((v) => !v)) return '';

  return config.format(values);
}
