import SwiftUI
import CoreUI
#if canImport(UIKit)
import UIKit
#endif

/// Live, copyable code snippet showing how to use the component with the
/// current knob values. Sits below the playground's States gallery.
struct CodeBlock: View {
    @Environment(\.colorScheme) private var scheme
    let code: String
    var language: String = "swift"

    @State private var copied: Bool = false

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            HStack {
                Text(language.uppercased())
                    .font(PrismaTypography.labelSm.font)
                    .foregroundStyle(PrismaSemanticColors.textTertiary.themed(scheme))
                Spacer()
                Button(action: copy) {
                    HStack(spacing: PrismaSpacing.sp1) {
                        Image(prisma: copied ? .check : .copy)
                            .renderingMode(.template)
                            .resizable()
                            .frame(width: 14, height: 14)
                            .foregroundStyle(
                                copied
                                    ? PrismaSemanticColors.statusSuccessDefault.themed(scheme)
                                    : PrismaSemanticColors.textSecondary.themed(scheme)
                            )
                        Text(copied ? "Copied" : "Copy")
                            .font(PrismaTypography.labelSm.font)
                            .foregroundStyle(
                                copied
                                    ? PrismaSemanticColors.statusSuccessDefault.themed(scheme)
                                    : PrismaSemanticColors.textSecondary.themed(scheme)
                            )
                    }
                    .padding(.horizontal, PrismaSpacing.sp2)
                    .padding(.vertical, PrismaSpacing.sp1)
                }
                .buttonStyle(.plain)
            }
            .padding(.horizontal, PrismaSpacing.sp4)
            .padding(.vertical, PrismaSpacing.sp2)

            ScrollView(.horizontal, showsIndicators: false) {
                Text(code)
                    .font(.system(.footnote, design: .monospaced))
                    .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))
                    .padding(PrismaSpacing.sp4)
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(PrismaSemanticColors.surfaceBase.themed(scheme))
        }
        .background(PrismaSemanticColors.surfaceSunken.themed(scheme))
        .clipShape(RoundedRectangle(cornerRadius: PrismaRadius.md))
        .overlay(
            RoundedRectangle(cornerRadius: PrismaRadius.md)
                .strokeBorder(PrismaSemanticColors.borderSubtle.themed(scheme), lineWidth: 1)
        )
    }

    private func copy() {
        #if canImport(UIKit)
        UIPasteboard.general.string = code
        #endif
        copied = true
        DispatchQueue.main.asyncAfter(deadline: .now() + 1.5) { copied = false }
    }
}
