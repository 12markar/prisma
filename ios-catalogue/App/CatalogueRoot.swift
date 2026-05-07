import SwiftUI
import CoreUI

struct CatalogueRoot: View {
    @Environment(\.colorScheme) private var scheme
    /// Persistent flag: once dismissed the onboarding never returns.
    @AppStorage("prisma.onboarding.dismissed") private var onboardingDismissed: Bool = false
    @State private var onboardingVisible: Bool = false
    /// Inspector visibility is intentionally NOT @AppStorage — it's a
    /// transient debug aid; coming back from background lands cleanly closed.
    var body: some View {
        ZStack {
            CatalogueShell()
            OnboardingOverlay(visible: $onboardingVisible)
        }
        // Cross-fade the entire tree on theme swap so the change reads
        // as a deliberate transition, not a snap. SwiftUI's view-tree
        // colour modifiers resolve at leaf level; this `.animation`
        // gates them collectively against the colorScheme key so they
        // ease together over 300ms.
        .animation(.easeInOut(duration: 0.3), value: scheme)
        .onAppear {
            if !onboardingDismissed { onboardingVisible = true }
        }
        .onChange(of: onboardingVisible) { _, isVisible in
            if !isVisible { onboardingDismissed = true }
        }
    }
}
