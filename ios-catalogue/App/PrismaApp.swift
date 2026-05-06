import SwiftUI
import CoreUI

@main
struct PrismaApp: App {

    /// User's explicit theme override.
    /// `nil` (absent key) means "follow system colorScheme"; `false` light; `true` dark.
    /// Persisted across launches via `@AppStorage`.
    @AppStorage("prisma.isDarkOverride") private var isDarkOverrideRaw: String = ""

    private var override: Bool? {
        switch isDarkOverrideRaw {
        case "light": return false
        case "dark":  return true
        default:      return nil
        }
    }

    var body: some Scene {
        WindowGroup {
            CatalogueRoot()
                .prismaTheme(isDarkOverride: override)
        }
    }
}
