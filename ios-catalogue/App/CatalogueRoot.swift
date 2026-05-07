import SwiftUI
import CoreUI

struct CatalogueRoot: View {
    @Environment(\.colorScheme) private var scheme
    /// Persistent flag: once dismissed the onboarding never returns.
    @AppStorage("prisma.onboarding.dismissed") private var onboardingDismissed: Bool = false
    @State private var onboardingVisible: Bool

    init() {
        // Read UserDefaults synchronously in init so the very first frame
        // already knows whether to show onboarding. Previously we set
        // `onboardingVisible = false` declaratively and flipped it in
        // .onAppear, which produced a visible blank ~couple-seconds beat
        // on cold start while the screen rendered empty before the
        // overlay faded in.
        let dismissed = UserDefaults.standard.bool(forKey: "prisma.onboarding.dismissed")
        _onboardingVisible = State(initialValue: !dismissed)
    }

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
        .onChange(of: onboardingVisible) { _, isVisible in
            if !isVisible { onboardingDismissed = true }
        }
    }
}
