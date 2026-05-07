package xyz.ksharma.prisma.catalogue.sdui

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

// Wire model — mirrors sdui-server/schema/sdui.schema.json. Keep the two in sync.
//
// kotlinx.serialization needs an explicit discriminator on each polymorphic
// hierarchy. Nodes use "type", actions use "kind". The default Json instance
// would force one or the other globally, so we annotate per-class instead.

@Serializable
public data class SduiDocument(
    val version: Int,
    val root: SduiNode,
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("type")
public sealed class SduiNode {

    @Serializable
    @SerialName("column")
    public data class Column(
        val children: List<SduiNode> = emptyList(),
        val spacing: Float? = null,
        val padding: Float? = null,
        val alignment: String? = null,
    ) : SduiNode()

    @Serializable
    @SerialName("row")
    public data class Row(
        val children: List<SduiNode> = emptyList(),
        val spacing: Float? = null,
        val alignment: String? = null,
    ) : SduiNode()

    @Serializable
    @SerialName("text")
    public data class Text(
        val value: String,
        val variant: String? = null,
        val color: String? = null,
        val align: String? = null,
    ) : SduiNode()

    @Serializable
    @SerialName("image")
    public data class Image(
        val url: String,
        val width: Float? = null,
        val height: Float? = null,
        val cornerRadius: Float? = null,
        val contentMode: String? = null,
        val alt: String? = null,
    ) : SduiNode()

    @Serializable
    @SerialName("button")
    public data class Button(
        val label: String,
        val variant: String? = null,
        val size: String? = null,
        val fullWidth: Boolean? = null,
        val disabled: Boolean? = null,
        val action: SduiAction? = null,
    ) : SduiNode()

    @Serializable
    @SerialName("card")
    public data class Card(
        val children: List<SduiNode> = emptyList(),
        val padding: Float? = null,
        val spacing: Float? = null,
    ) : SduiNode()

    @Serializable
    @SerialName("tabs")
    public data class Tabs(
        val items: List<TabItem>,
        val selectedIndex: Int = 0,
    ) : SduiNode()

    @Serializable
    @SerialName("divider")
    public data object Divider : SduiNode()

    @Serializable
    @SerialName("spacer")
    public data class Spacer(val size: Float? = null) : SduiNode()

    @Serializable
    @SerialName("badge")
    public data class Badge(
        val label: String,
        val tone: String? = null,
    ) : SduiNode()

    @Serializable
    @SerialName("listItem")
    public data class ListItem(
        val title: String,
        val subtitle: String? = null,
        val leadingIconUrl: String? = null,
        val trailingText: String? = null,
        val showChevron: Boolean? = null,
        val action: SduiAction? = null,
    ) : SduiNode()
}

@Serializable
public data class TabItem(
    val label: String,
    val content: SduiNode,
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("kind")
public sealed class SduiAction {

    @Serializable
    @SerialName("noop")
    public data object Noop : SduiAction()

    @Serializable
    @SerialName("openUrl")
    public data class OpenUrl(val url: String) : SduiAction()

    @Serializable
    @SerialName("log")
    public data class Log(val id: String) : SduiAction()
}
