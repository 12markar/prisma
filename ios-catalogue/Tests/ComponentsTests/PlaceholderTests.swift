import XCTest
@testable import Components

final class PlaceholderTests: XCTestCase {
    func testVersionStringSet() {
        XCTAssertFalse(PrismaComponents.version.isEmpty)
    }
}
