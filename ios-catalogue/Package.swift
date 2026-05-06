// swift-tools-version:5.10
import PackageDescription

let package = Package(
    name: "Prisma",
    platforms: [.iOS(.v17), .macOS(.v14)],
    products: [
        .library(name: "CoreUI", targets: ["CoreUI"]),
        .library(name: "Components", targets: ["Components"]),
    ],
    targets: [
        .target(
            name: "CoreUI",
            path: "Sources/CoreUI"
        ),
        .target(
            name: "Components",
            dependencies: ["CoreUI"],
            path: "Sources/Components"
        ),
        .testTarget(
            name: "ComponentsTests",
            dependencies: ["Components"],
            path: "Tests/ComponentsTests"
        ),
    ]
)
