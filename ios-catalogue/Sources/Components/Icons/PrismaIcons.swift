import SwiftUI

// AUTO-GENERATED from Prisma icon set (64 icons). Do not hand-edit;
// run `npm run build-icons` from design-system/.
//
// Usage in SwiftUI:
//   Image(prisma: .search)
//       .renderingMode(.template)
//       .foregroundStyle(PrismaSemanticColors.textPrimary.themed(scheme))

public enum PrismaIcon: String, CaseIterable {
    case alert = "alert"
    case archive = "archive"
    case arrowDown = "arrow-down"
    case arrowLeft = "arrow-left"
    case arrowRight = "arrow-right"
    case arrowUp = "arrow-up"
    case barcode = "barcode"
    case bell = "bell"
    case bookmark = "bookmark"
    case calendar = "calendar"
    case check = "check"
    case chevronDown = "chevron-down"
    case chevronLeft = "chevron-left"
    case chevronRight = "chevron-right"
    case chevronUp = "chevron-up"
    case clock = "clock"
    case close = "close"
    case copy = "copy"
    case doc = "doc"
    case download = "download"
    case edit = "edit"
    case error = "error"
    case eyeOff = "eye-off"
    case eye = "eye"
    case file = "file"
    case filter = "filter"
    case folder = "folder"
    case grid = "grid"
    case heart = "heart"
    case help = "help"
    case image = "image"
    case info = "info"
    case label = "label"
    case layers = "layers"
    case link = "link"
    case list = "list"
    case location = "location"
    case lock = "lock"
    case mail = "mail"
    case message = "message"
    case minus = "minus"
    case more = "more"
    case phone = "phone"
    case plus = "plus"
    case qr = "qr"
    case refresh = "refresh"
    case save = "save"
    case scan = "scan"
    case search = "search"
    case settings = "settings"
    case share = "share"
    case sort = "sort"
    case star = "star"
    case success = "success"
    case sync = "sync"
    case tag = "tag"
    case trash = "trash"
    case unlock = "unlock"
    case upload = "upload"
    case user = "user"
    case users = "users"
    case video = "video"
    case warning = "warning"
    case x = "x"
}

public extension Image {
    /// Initialise an Image from a Prisma icon symbol. Asset catalog
    /// PrismaIcons.xcassets is bundled with the Components SPM target.
    init(prisma icon: PrismaIcon) {
        self.init(icon.rawValue, bundle: .module)
    }
}
