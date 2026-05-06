// Compose format — emits PrismaTokens.kt
//
// Output is a single Kotlin file with grouped objects:
//   - PrismaPrimitiveColors (flat hex constants)
//   - PrismaSemanticColor   (data class with light/dark + @Composable resolve())
//   - PrismaSemanticColors  (instances per semantic alias)
//   - PrismaSpacing         (Dp constants)
//   - PrismaRadius          (Dp constants)
//   - PrismaTypography      (TextStyle constants — references PrismaFonts)
//   - PrismaShadow / PrismaElevations (multi-layer shadow specs, light/dark)
//   - PrismaMotion          (Duration ms + cubic-bezier params)
//
// PrismaFonts.kt is hand-written in :core-ui (registers FontFamily for Instrument Sans + JetBrains Mono).

import {
  hexToARGB,
  parsePx,
  parseMs,
  resolveLineHeight,
  resolveEm,
  pascalCase,
  fontFamilyKey,
  parseCubicBezier,
  banner,
} from './utils.mjs';

const KT_PACKAGE = 'xyz.ksharma.prisma.tokens';

// Walk a nested token group (e.g. color.primitive.neutral) and yield [pathArr, leafToken].
function* walkLeaves(node, path = []) {
  if (node && typeof node === 'object' && '$value' in node) {
    yield [path, node];
    return;
  }
  if (node && typeof node === 'object') {
    for (const [k, v] of Object.entries(node)) {
      if (k.startsWith('$')) continue;
      yield* walkLeaves(v, [...path, k]);
    }
  }
}

const colorLiteral = (v) => `Color(${hexToARGB(v)})`;

const formatPrimitiveColors = (colors) => {
  const lines = [];
  lines.push(`object PrismaPrimitiveColors {`);
  for (const family of Object.keys(colors)) {
    if (family.startsWith('$')) continue;
    lines.push(`  // ${family}`);
    for (const [path, token] of walkLeaves(colors[family])) {
      const name = pascalCase([family, ...path]);
      lines.push(`  val ${name}: Color = ${colorLiteral(token.$value)}`);
    }
    lines.push('');
  }
  lines.push(`}`);
  return lines.join('\n');
};

const formatSemanticColors = (semantic) => {
  const lines = [];
  lines.push(
    `/** A colour that resolves differently in light vs. dark mode. */`,
    `@Stable`,
    `data class PrismaSemanticColor(val light: Color, val dark: Color) {`,
    `  @Composable fun resolve(): Color = if (isSystemInDarkTheme()) dark else light`,
    `  fun resolve(isDark: Boolean): Color = if (isDark) dark else light`,
    `}`,
    ``,
    `object PrismaSemanticColors {`,
  );
  for (const [path, token] of walkLeaves(semantic)) {
    const name = pascalCase(path);
    const v = token.$value;
    if (v && typeof v === 'object' && 'light' in v && 'dark' in v) {
      lines.push(
        `  val ${name}: PrismaSemanticColor = PrismaSemanticColor(`,
        `    light = ${colorLiteral(v.light)},`,
        `    dark  = ${colorLiteral(v.dark)},`,
        `  )`,
      );
    } else {
      // Single-value semantic — wrap as same-on-both-modes
      const c = colorLiteral(v);
      lines.push(`  val ${name}: PrismaSemanticColor = PrismaSemanticColor(light = ${c}, dark = ${c})`);
    }
  }
  lines.push(`}`);
  return lines.join('\n');
};

const formatDimensions = (objectName, group, prefix = '') => {
  const lines = [`object ${objectName} {`];
  for (const [path, token] of walkLeaves(group)) {
    const name = prefix + pascalCase(path);
    const px = parsePx(token.$value);
    if (Number.isNaN(px)) {
      lines.push(`  // skipped: ${name} (unparseable value ${JSON.stringify(token.$value)})`);
      continue;
    }
    lines.push(`  val ${name}: Dp = ${px}.dp`);
  }
  lines.push(`}`);
  return lines.join('\n');
};

const formatTypography = (typography, fonts) => {
  const lines = [
    `/**`,
    ` * Typography tokens. References [PrismaFonts] which is hand-written in :core-ui`,
    ` * and registers FontFamily definitions for Instrument Sans and JetBrains Mono.`,
    ` */`,
    `object PrismaTypography {`,
  ];

  for (const [path, token] of walkLeaves(typography)) {
    const name = pascalCase(path);
    const v = token.$value || {};
    const sizePx = parsePx(v.fontSize);
    const lhPx = resolveLineHeight(v.lineHeight, sizePx);
    const lsPx = v.letterSpacing ? resolveEm(v.letterSpacing, sizePx) : 0;
    const familyKey = fontFamilyKey(v.fontFamily);
    const weight = parseInt(v.fontWeight, 10) || 400;

    lines.push(
      `  val ${name}: TextStyle = TextStyle(`,
      `    fontFamily = PrismaFonts.${familyKey},`,
      `    fontWeight = FontWeight(${weight}),`,
      `    fontSize = ${sizePx}.sp,`,
      `    lineHeight = ${Number(lhPx.toFixed(2))}.sp,`,
      `    letterSpacing = ${Number(lsPx.toFixed(3))}.sp,`,
      `  )`,
    );
  }
  lines.push(`}`);
  return lines.join('\n');
};

const formatShadow = (s) => {
  const inset = s.inset ? ', inset = true' : '';
  return `PrismaShadow(offsetY = ${parsePx(s.offsetY)}.dp, blur = ${parsePx(s.blur)}.dp, spread = ${parsePx(s.spread)}.dp, color = ${colorLiteral(s.color)}${inset})`;
};

const formatElevations = (elevation) => {
  const lines = [
    `/**`,
    ` * Multi-layer shadow specification. Render via Modifier.prismaShadow(level) defined in :core-ui.`,
    ` */`,
    `data class PrismaShadow(`,
    `  val offsetY: Dp,`,
    `  val blur: Dp,`,
    `  val spread: Dp,`,
    `  val color: Color,`,
    `  val inset: Boolean = false,`,
    `)`,
    ``,
    `data class PrismaElevation(val light: List<PrismaShadow>, val dark: List<PrismaShadow>)`,
    ``,
    `object PrismaElevations {`,
  ];
  for (const [path, token] of walkLeaves(elevation)) {
    const name = `Level${pascalCase(path)}`;
    const v = token.$value;
    const lightLayers = (v.light || []).map(formatShadow).join(', ');
    const darkLayers = (v.dark || []).map(formatShadow).join(', ');
    lines.push(
      `  val ${name}: PrismaElevation = PrismaElevation(`,
      `    light = listOf(${lightLayers}),`,
      `    dark  = listOf(${darkLayers}),`,
      `  )`,
    );
  }
  lines.push(`}`);
  return lines.join('\n');
};

const formatMotion = (motion) => {
  const lines = [
    `object PrismaMotion {`,
    `  object Duration {`,
  ];
  for (const [k, t] of Object.entries(motion.duration || {})) {
    if (k.startsWith('$')) continue;
    lines.push(`    const val ${pascalCase([k])}: Int = ${parseMs(t.$value)} // ms`);
  }
  lines.push(`  }`);
  lines.push(``, `  /** Cubic Bézier control points (x1, y1, x2, y2). Use Compose CubicBezierEasing. */`);
  lines.push(`  object Easing {`);
  for (const [k, t] of Object.entries(motion.easing || {})) {
    if (k.startsWith('$')) continue;
    const [a, b, c, d] = parseCubicBezier(t.$value);
    lines.push(`    val ${pascalCase([k])}: FloatArray = floatArrayOf(${a}f, ${b}f, ${c}f, ${d}f)`);
  }
  lines.push(`  }`);
  lines.push(`}`);
  return lines.join('\n');
};

export function generateComposeFile(tokens) {
  const sections = [];
  sections.push(banner('kotlin'));
  sections.push(`@file:Suppress("MagicNumber", "TopLevelPropertyNaming", "MaxLineLength", "LongLine")`);
  sections.push(``);
  sections.push(`package ${KT_PACKAGE}`);
  sections.push(``);
  sections.push(
    `import androidx.compose.foundation.isSystemInDarkTheme`,
    `import androidx.compose.runtime.Composable`,
    `import androidx.compose.runtime.Stable`,
    `import androidx.compose.ui.graphics.Color`,
    `import androidx.compose.ui.text.TextStyle`,
    `import androidx.compose.ui.text.font.FontWeight`,
    `import androidx.compose.ui.unit.Dp`,
    `import androidx.compose.ui.unit.dp`,
    `import androidx.compose.ui.unit.sp`,
  );
  sections.push(``);

  if (tokens.color?.primitive) sections.push(formatPrimitiveColors(tokens.color.primitive), '');
  if (tokens.color?.semantic) sections.push(formatSemanticColors(tokens.color.semantic), '');
  if (tokens.spacing) sections.push(formatDimensions('PrismaSpacing', tokens.spacing, 'Sp'), '');
  if (tokens.radius) sections.push(formatDimensions('PrismaRadius', tokens.radius), '');
  if (tokens.typography) sections.push(formatTypography(tokens.typography, tokens.font), '');
  if (tokens.elevation) sections.push(formatElevations(tokens.elevation), '');
  if (tokens.motion) sections.push(formatMotion(tokens.motion), '');

  return sections.join('\n');
}
