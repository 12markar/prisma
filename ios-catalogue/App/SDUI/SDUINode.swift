import Foundation

// Wire model — mirrors sdui-server/schema/sdui.schema.json. Keep in sync.
//
// Swift's Codable doesn't support discriminator-based polymorphism out of
// the box, so we hand-roll Decodable on the enum: read `type` from a keyed
// container, then decode the rest of the object as the matching payload.

struct SDUIDocument: Decodable {
    let version: Int
    let root: SDUINode
}

indirect enum SDUINode: Decodable {
    case column(SDUIColumn)
    case row(SDUIRow)
    case text(SDUIText)
    case image(SDUIImage)
    case button(SDUIButton)
    case card(SDUICard)
    case tabs(SDUITabs)
    case divider
    case spacer(SDUISpacer)
    case badge(SDUIBadge)
    case listItem(SDUIListItem)
    case unsupported(String)

    private enum DiscriminatorKeys: String, CodingKey { case type }

    init(from decoder: Decoder) throws {
        let keyed = try decoder.container(keyedBy: DiscriminatorKeys.self)
        let type  = try keyed.decode(String.self, forKey: .type)
        let single = try decoder.singleValueContainer()
        switch type {
        case "column":   self = .column(try single.decode(SDUIColumn.self))
        case "row":      self = .row(try single.decode(SDUIRow.self))
        case "text":     self = .text(try single.decode(SDUIText.self))
        case "image":    self = .image(try single.decode(SDUIImage.self))
        case "button":   self = .button(try single.decode(SDUIButton.self))
        case "card":     self = .card(try single.decode(SDUICard.self))
        case "tabs":     self = .tabs(try single.decode(SDUITabs.self))
        case "divider":  self = .divider
        case "spacer":   self = .spacer(try single.decode(SDUISpacer.self))
        case "badge":    self = .badge(try single.decode(SDUIBadge.self))
        case "listItem": self = .listItem(try single.decode(SDUIListItem.self))
        default:         self = .unsupported(type)
        }
    }
}

struct SDUIColumn: Decodable {
    let children: [SDUINode]
    let spacing: Double?
    let padding: Double?
    let alignment: String?
}

struct SDUIRow: Decodable {
    let children: [SDUINode]
    let spacing: Double?
    let alignment: String?
}

struct SDUIText: Decodable {
    let value: String
    let variant: String?
    let color: String?
    let align: String?
}

struct SDUIImage: Decodable {
    let url: String
    let width: Double?
    let height: Double?
    let cornerRadius: Double?
    let contentMode: String?
    let alt: String?
}

struct SDUIButton: Decodable {
    let label: String
    let variant: String?
    let size: String?
    let fullWidth: Bool?
    let disabled: Bool?
    let action: SDUIAction?
}

struct SDUICard: Decodable {
    let children: [SDUINode]
    let padding: Double?
    let spacing: Double?
}

struct SDUITabs: Decodable {
    let items: [SDUITabItem]
    let selectedIndex: Int?
}

struct SDUITabItem: Decodable {
    let label: String
    let content: SDUINode
}

struct SDUISpacer: Decodable {
    let size: Double?
}

struct SDUIBadge: Decodable {
    let label: String
    let tone: String?
}

struct SDUIListItem: Decodable {
    let title: String
    let subtitle: String?
    let leadingIconUrl: String?
    let trailingText: String?
    let showChevron: Bool?
    let action: SDUIAction?
}

enum SDUIAction: Decodable {
    case noop
    case openUrl(URL)
    case log(String)

    private enum Keys: String, CodingKey { case kind, url, id }

    init(from decoder: Decoder) throws {
        let c = try decoder.container(keyedBy: Keys.self)
        let kind = try c.decode(String.self, forKey: .kind)
        switch kind {
        case "noop":    self = .noop
        case "openUrl":
            let raw = try c.decode(String.self, forKey: .url)
            self = .openUrl(URL(string: raw) ?? URL(fileURLWithPath: "/"))
        case "log":     self = .log(try c.decode(String.self, forKey: .id))
        default:        self = .noop
        }
    }
}
