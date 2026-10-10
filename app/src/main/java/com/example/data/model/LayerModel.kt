package com.example.data.model

/**
 * Represents an individual layer on the editor canvas (either a Text element or a Sticker element).
 */
sealed class LayerItem {
    abstract val id: String
    abstract val displayName: String
    abstract val isVisible: Boolean
    abstract val isLocked: Boolean
    abstract val layerType: String

    data class Text(
        val textBox: MemeTextBox
    ) : LayerItem() {
        override val id: String get() = textBox.id
        override val displayName: String
            get() = when {
                textBox.id == "top_text" -> if (textBox.text.isNotBlank()) "Top: ${textBox.text.take(16)}" else "Top Caption"
                textBox.id == "bottom_text" -> if (textBox.text.isNotBlank()) "Bottom: ${textBox.text.take(16)}" else "Bottom Caption"
                textBox.text.isNotBlank() -> textBox.text.take(20)
                else -> "Text Layer"
            }
        override val isVisible: Boolean get() = textBox.isVisible
        override val isLocked: Boolean get() = textBox.isLocked
        override val layerType: String get() = "Text"
    }

    data class Sticker(
        val sticker: PlacedSticker
    ) : LayerItem() {
        override val id: String get() = sticker.id
        override val displayName: String
            get() = when {
                sticker.emoji.isNotBlank() -> "Sticker ${sticker.emoji}"
                sticker.label.isNotBlank() -> sticker.label
                else -> "Sticker Layer"
            }
        override val isVisible: Boolean get() = sticker.isVisible
        override val isLocked: Boolean get() = sticker.isLocked
        override val layerType: String get() = "Sticker"
    }
}

/**
 * Reordering actions supported by the Layer Management Sidebar.
 */
enum class LayerOrderAction {
    BRING_TO_FRONT,
    SEND_TO_BACK,
    MOVE_UP,      // Bring forward 1 step
    MOVE_DOWN     // Send backward 1 step
}

object LayerUtils {

    /**
     * Reorders an ID in a list according to the specified LayerOrderAction.
     * The order list represents drawing order from index 0 (back) to index size-1 (front).
     */
    fun reorder(order: List<String>, id: String, action: LayerOrderAction): List<String> {
        val currentList = order.toMutableList()
        val index = currentList.indexOf(id)
        if (index == -1) return currentList

        when (action) {
            LayerOrderAction.BRING_TO_FRONT -> {
                // Move to the very end of the list (drawn on top)
                if (index < currentList.lastIndex) {
                    val item = currentList.removeAt(index)
                    currentList.add(item)
                }
            }
            LayerOrderAction.SEND_TO_BACK -> {
                // Move to index 0 (drawn at the bottom)
                if (index > 0) {
                    val item = currentList.removeAt(index)
                    currentList.add(0, item)
                }
            }
            LayerOrderAction.MOVE_UP -> {
                // Bring forward towards the end by 1 step
                if (index < currentList.lastIndex) {
                    val nextIndex = index + 1
                    val item = currentList.removeAt(index)
                    currentList.add(nextIndex, item)
                }
            }
            LayerOrderAction.MOVE_DOWN -> {
                // Send backward towards index 0 by 1 step
                if (index > 0) {
                    val prevIndex = index - 1
                    val item = currentList.removeAt(index)
                    currentList.add(prevIndex, item)
                }
            }
        }
        return currentList
    }

    /**
     * Builds and sorts a unified list of LayerItems from text boxes and stickers,
     * maintaining consistency with the specified layerOrder list.
     * Returns layers sorted from bottom (back, index 0) to top (front, last index).
     */
    fun buildSortedLayers(
        textBoxes: List<MemeTextBox>,
        stickers: List<PlacedSticker>,
        layerOrder: List<String>
    ): List<LayerItem> {
        val allItems = mutableListOf<LayerItem>()
        textBoxes.forEach { allItems.add(LayerItem.Text(it)) }
        stickers.forEach { allItems.add(LayerItem.Sticker(it)) }

        return if (layerOrder.isNotEmpty()) {
            allItems.sortedBy { item ->
                val idx = layerOrder.indexOf(item.id)
                if (idx != -1) idx else Int.MAX_VALUE
            }
        } else {
            allItems
        }
    }
}
