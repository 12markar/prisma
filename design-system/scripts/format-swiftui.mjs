// SwiftUI format — emits PrismaTokens.swift
//
// Output is a single Swift file with grouped enums/structs:
//   - PrismaPrimitiveColors (static Color constants)
//   - PrismaSemanticColor   (struct with light/dark + .resolve(scheme:))
//   - PrismaSemanticColors  (instances per semantic alias)
//   - PrismaSpacing / PrismaRadius (CGFloat constants)
//   - PrismaTypography      (Font constants — references PrismaFonts)
//   - PrismaShadow / PrismaElevations (multi-layer shadow specs, light/dark)
//   - PrismaMotion          (Duration ms + cubic-bezier UnitCurve)
//
// PrismaFonts.swift is hand-written in CoreUI; declares font names registered via Resources/Fonts/.

import {
  hexToRGBA,
  parsePx,
  parseMs,
  resolveLineHeight,
  resolveEm,
  pascalCase,
  fontFamilyKey,
  parseCubicBezier,
  banner,
  f,
} from './utils.mjs';

// Swift reserved keywords that need backtick-escaping when used as identifiers.
const SWIFT_RESERVED = new Set([
  'default', 'class', 'struct', 'enum', 'func', 'var', 'let', 'if', 'else',
  'switch', 'case', 'for', 'while', 'do', 'return', 'break', 'continue',
  'in', 'is', 'as', 'nil', 'true', 'false', 'self', 'Self', 'super',
  'init', 'deinit', 'import', 'where', 'throws', 'try', 'throw', 'protocol',
  'extension', 'subscript', 'typealias', 'public', 'private', 'internal',
  'fileprivate', 'open', 'static', 'final', 'lazy', 'weak', 'unowned',
  'inout', 'operator', 'precedencegroup', 'associatedtype', 'rethrows',
  'guard', 'defer', 'catch', 'repeat', 'any', 'some',
]);

const escapeSwift = (name) => (SWIFT_RESERVED.has(name) ? `\`${name}\`` : name);

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

const colorLiteral = (v) => {
  const { r, g, b, a } = hexToRGBA(v);
  return `Color(.sRGB, red: ${f(r)}, green: ${f(g)}, blue: ${f(b)}, opacity: ${f(a)})`;
};

const formatPrimitiveColors = (colors) => {
  const lines = [`public enum PrismaPrimitiveColors {`];
  for (const family of Object.keys(colors)) {
    if (family.startsWith('$')) continue;
    lines.push(`  // MARK: - ${family}`);
    for (const [path, token] of walkLeaves(colors[family])) {
      const name = pascalCase([family, ...path]);
      lines.push(`  public static let ${name.charAt(0).toLowerCase() + name.slice(1)}: Color = ${colorLiteral(token.$value)}`);
    }
    lines.push('');
  }
  lines.push(`}`);
  return lines.join('\n');
};

const formatSemanticColors = (semantic) => {
  const lines = [
    `/// A colour that resolves differently in light vs. dark mode.`,
    `public struct PrismaSemanticColor: Equatable {`,
    `  public let light: Color`,
    `  public let dark: Color`,
    `  public init(light: Color, dark: Color) { self.light = light; self.dark = dark }`,
    `  public func resolve(_ scheme: ColorScheme) -> Color { scheme == .dark ? dark : light }`,
    `}`,
    ``,
    `public enum PrismaSemanticColors {`,
  ];
  for (const [path, token] of walkLeaves(semantic)) {
    const name = pascalCase(path);
    const lower = name.charAt(0).toLowerCase() + name.slice(1);
    const v = token.$value;
    if (v && typeof v === 'object' && 'light' in v && 'dark' in v) {
      lines.push(
        `  public static let ${lower}: PrismaSemanticColor = PrismaSemanticColor(`,
        `    light: ${colorLiteral(v.light)},`,
        `    dark:  ${colorLiteral(v.dark)}`,
        `  )`,
      );
    } else {
      const c = colorLiteral(v);
      lines.push(`  public static let ${lower}: PrismaSemanticColor = PrismaSemanticColor(light: ${c}, dark: ${c})`);
    }
  }
  lines.push(`}`);
  return lines.join('\n');
};

const formatDimensions = (enumName, group, prefix = '') => {
  const lines = [`public enum ${enumName} {`];
  for (const [path, token] of walkLeaves(group)) {
    const pascalName = prefix + pascalCase(path);
    const camelName = escapeSwift(pascalName.charAt(0).toLowerCase() + pascalName.slice(1));
    const px = parsePx(token.$value);
    if (Number.isNaN(px)) {
      lines.push(`  // skipped: ${camelName} (unparseable value ${JSON.stringify(token.$value)})`);
      continue;
    }
    lines.push(`  public static let ${camelName}: CGFloat = ${px}`);
  }
  lines.push(`}`);
  return lines.join('\n');
};

const formatTypography = (typography) => {
  const lines = [
    `/// Typography tokens. References [PrismaFonts] which is hand-written in CoreUI`,
    `/// and registers Instrument Sans + JetBrains Mono via Resources/Fonts/.`,
    `public enum PrismaTypography {`,
    `  public struct Style: Equatable {`,
    `    public let font: Font`,
    `    public let lineHeight: CGFloat`,
    `    public let letterSpacing: CGFloat`,
    `    public init(font: Font, lineHeight: CGFloat, letterSpacing: CGFloat) {`,
    `      self.font = font; self.lineHeight = lineHeight; self.letterSpacing = letterSpacing`,
    `    }`,
    `  }`,
    ``,
  ];
  for (const [path, token] of walkLeaves(typography)) {
    const name = pascalCase(path);
    const lower = name.charAt(0).toLowerCase() + name.slice(1);
    const v = token.$value || {};
    const sizePx = parsePx(v.fontSize);
    const lhPx = resolveLineHeight(v.lineHeight, sizePx);
    const lsPx = v.letterSpacing ? resolveEm(v.letterSpacing, sizePx) : 0;
    const familyKey = fontFamilyKey(v.fontFamily);
    const weight = swiftWeightFor(v.fontWeight);
    lines.push(
      `  public static let ${lower}: Style = Style(`,
      `    font: PrismaFonts.${familyKey.toLowerCase()}(size: ${sizePx}, weight: ${weight}),`,
      `    lineHeight: ${Number(lhPx.toFixed(2))},`,
      `    letterSpacing: ${Number(lsPx.toFixed(3))}`,
      `  )`,
    );
  }
  lines.push(`}`);
  return lines.join('\n');
};

const swiftWeightFor = (w) => {
  const n = parseInt(w, 10);
  if (n >= 800) return '.heavy';
  if (n >= 700) return '.bold';
  if (n >= 600) return '.semibold';
  if (n >= 500) return '.medium';
  if (n >= 400) return '.regular';
  if (n >= 300) return '.light';
  if (n >= 200) return '.thin';
  return '.regular';
};

const formatShadow = (s) => {
  const inset = s.inset === true ? ', inset: true' : '';
  return `PrismaShadow(offsetY: ${parsePx(s.offsetY)}, blur: ${parsePx(s.blur)}, spread: ${parsePx(s.spread)}, color: ${colorLiteral(s.color)}${inset})`;
};

const formatElevations = (elevation) => {
  const lines = [
    `public struct PrismaShadow: Equatable {`,
    `  public let offsetY: CGFloat`,
    `  public let blur: CGFloat`,
    `  public let spread: CGFloat`,
    `  public let color: Color`,
    `  public let inset: Bool`,
    `  public init(offsetY: CGFloat, blur: CGFloat, spread: CGFloat, color: Color, inset: Bool = false) {`,
    `    self.offsetY = offsetY; self.blur = blur; self.spread = spread; self.color = color; self.inset = inset`,
    `  }`,
    `}`,
    ``,
    `public struct PrismaElevation: Equatable {`,
    `  public let light: [PrismaShadow]`,
    `  public let dark: [PrismaShadow]`,
    `  public init(light: [PrismaShadow], dark: [PrismaShadow]) { self.light = light; self.dark = dark }`,
    `}`,
    ``,
    `public enum PrismaElevations {`,
  ];
  for (const [path, token] of walkLeaves(elevation)) {
    const name = `level${pascalCase(path)}`;
    const v = token.$value;
    const lightLayers = (v.light || []).map(formatShadow).join(', ');
    const darkLayers = (v.dark || []).map(formatShadow).join(', ');
    lines.push(
      `  public static let ${name}: PrismaElevation = PrismaElevation(`,
      `    light: [${lightLayers}],`,
      `    dark:  [${darkLayers}]`,
      `  )`,
    );
  }
  lines.push(`}`);
  return lines.join('\n');
};

const formatMotion = (motion) => {
  const lines = [
    `public enum PrismaMotion {`,
    `  /// Durations in seconds (suitable for SwiftUI Animation).`,
    `  public enum Duration {`,
  ];
  for (const [k, t] of Object.entries(motion.duration || {})) {
    if (k.startsWith('$')) continue;
    const ms = parseMs(t.$value);
    const seconds = Number((ms / 1000).toFixed(3));
    const camel = escapeSwift(k.charAt(0).toLowerCase() + k.slice(1));
    lines.push(`    public static let ${camel}: Double = ${seconds} // ${ms}ms`);
  }
  lines.push(`  }`);
  lines.push(``, `  /// Cubic Bézier control points (x1, y1, x2, y2). Build SwiftUI Animation.timingCurve.`);
  lines.push(`  public enum Easing {`);
  for (const [k, t] of Object.entries(motion.easing || {})) {
    if (k.startsWith('$')) continue;
    const [a, b, c, d] = parseCubicBezier(t.$value);
    const camel = escapeSwift(k.charAt(0).toLowerCase() + k.slice(1));
    lines.push(`    public static let ${camel}: (Double, Double, Double, Double) = (${a}, ${b}, ${c}, ${d})`);
  }
  lines.push(`  }`);
  lines.push(`}`);
  return lines.join('\n');
};

export function generateSwiftUIFile(tokens) {
  const sections = [];
  sections.push(banner('swift'));
  sections.push(``);
  sections.push(`import SwiftUI`);
  sections.push(``);

  if (tokens.color?.primitive) sections.push(formatPrimitiveColors(tokens.color.primitive), '');
  if (tokens.color?.semantic) sections.push(formatSemanticColors(tokens.color.semantic), '');
  if (tokens.spacing) sections.push(formatDimensions('PrismaSpacing', tokens.spacing, 'sp'), '');
  if (tokens.radius) sections.push(formatDimensions('PrismaRadius', tokens.radius), '');
  if (tokens.typography) sections.push(formatTypography(tokens.typography), '');
  if (tokens.elevation) sections.push(formatElevations(tokens.elevation), '');
  if (tokens.motion) sections.push(formatMotion(tokens.motion), '');

  return sections.join('\n');
}
