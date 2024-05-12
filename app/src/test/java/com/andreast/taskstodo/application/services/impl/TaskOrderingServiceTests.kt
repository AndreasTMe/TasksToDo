package com.andreast.taskstodo.application.services.impl

import com.andreast.taskstodo.application.dto.Level
import com.andreast.taskstodo.application.dto.TaskListItemDto
import org.junit.Test

class TaskOrderingServiceTests {

    private val sut = TaskOrderingService()

    @Test
    fun calculateOrderForNewItem_parentIdDoesNotExist_shouldReturnNegativeOne() {
        // Arrange
        val parentId: Long = 123
        val items = listOf(
            TaskListItemDto(id = 0),
            TaskListItemDto(id = 1),
            TaskListItemDto(id = 2),
        )

        // Act
        val result = sut.calculateOrderForNewItem(parentId, items)

        // Assert
        assert(result == -1)
    }

    @Test
    fun calculateOrderForNewItem_itemsListIsEmpty_shouldReturnZero() {
        // Arrange
        val parentId: Long = 123
        val items = listOf<TaskListItemDto>()

        // Act
        val result = sut.calculateOrderForNewItem(parentId, items)

        // Assert
        assert(result == 0)
    }

    @Test
    fun calculateOrderForNewItem_parentIdFoundButNoChildren_shouldReturnZero() {
        // Arrange
        val parentId: Long = 1
        val items = listOf(
            TaskListItemDto(id = 0),
            TaskListItemDto(id = parentId),
            TaskListItemDto(id = 2),
        )

        // Act
        val result = sut.calculateOrderForNewItem(parentId, items)

        // Assert
        assert(result == 0)
    }

    @Test
    fun calculateOrderForNewItem_parentIdFoundWithTwoChildren_shouldReturnMaxOrderPlusOne() {
        // Arrange
        val parentId: Long = 1
        val maxOrder = 69
        val items = listOf(
            TaskListItemDto(id = 0),
            TaskListItemDto(id = parentId),
            TaskListItemDto(id = 2),
            TaskListItemDto(id = 3, parentId = parentId, order = maxOrder - 1),
            TaskListItemDto(id = 4, parentId = parentId, order = maxOrder),
        )

        // Act
        val result = sut.calculateOrderForNewItem(parentId, items)

        // Assert
        assert(result == maxOrder + 1)
    }

    @Test
    fun reorderTasks_fromEqualsTo_returnEmptyList() {
        // Arrange
        val from = 1
        val to = 1
        val items = listOf(
            TaskListItemDto(id = 0),
            TaskListItemDto(id = 1),
            TaskListItemDto(id = 2),
        )

        // Act
        val result = sut.reorderTasks(from, to, items)

        // Assert
        assert(result.isEmpty())
    }

    @Test
    fun reorderTasks_fromDoesNotExistInIndices_returnEmptyList() {
        // Arrange
        val from = 69
        val to = 1
        val items = listOf(
            TaskListItemDto(id = 0),
            TaskListItemDto(id = 1),
            TaskListItemDto(id = 2),
        )

        // Act
        val result = sut.reorderTasks(from, to, items)

        // Assert
        assert(result.isEmpty())
    }

    @Test
    fun reorderTasks_toDoesNotExistInIndices_returnEmptyList() {
        // Arrange
        val from = 1
        val to = 69
        val items = listOf(
            TaskListItemDto(id = 0),
            TaskListItemDto(id = 1),
            TaskListItemDto(id = 2),
        )

        // Act
        val result = sut.reorderTasks(from, to, items)

        // Assert
        assert(result.isEmpty())
    }

    @Test
    fun canReorder_tryingToPlaceFromItemBetweenItsChildren_returnFalse() {
        // Arrange
        val from = 0
        val to = 1
        val items = listOf(
            TaskListItemDto(id = 0, order = 0, level = Level.Zero),  // from
            TaskListItemDto(id = 1, parentId = 0, order = 0, level = Level.One), // to
            TaskListItemDto(id = 2, parentId = 0, order = 1, level = Level.One),
        )
        val shouldBecomeChild = true

        // Act
        val result = sut.canReorderWhenMovingDown(from, to, items, shouldBecomeChild)

        // Assert
        assert(!result)
    }

    @Test
    fun canReorder_tooManyChildrenLevels_returnFalse() {
        // Arrange
        val from = 0
        val to = 7
        val items = listOf(
            TaskListItemDto(id = 0, order = 0, level = Level.Zero), // from
            TaskListItemDto(id = 1, parentId = 0, order = 0, level = Level.One),
            TaskListItemDto(id = 2, parentId = 1, order = 0, level = Level.Two),
            TaskListItemDto(id = 3, parentId = 2, order = 0, level = Level.Three),
            TaskListItemDto(id = 4, parentId = 3, order = 0, level = Level.Four),
            TaskListItemDto(id = 5, order = 1, level = Level.Zero),
            TaskListItemDto(id = 6, parentId = 5, order = 0, level = Level.One),
            TaskListItemDto(id = 7, parentId = 6, order = 0, level = Level.Two), // to
        )
        val shouldBecomeChild = false

        // Act
        val result = sut.canReorderWhenMovingDown(from, to, items, shouldBecomeChild)

        // Assert
        assert(!result)
    }

    @Test
    fun reorderTasks_moveTaskWithSameAncestorDownAndBetweenSameLevelTasks_returnReorderedTasks() {
        // Arrange
        val from = 1
        val to = 3
        val items = listOf(
            TaskListItemDto(id = 0, order = 0, level = Level.Zero),
            TaskListItemDto(id = 1, parentId = 0, order = 0, level = Level.One), // from
            TaskListItemDto(id = 2, parentId = 0, order = 1, level = Level.One),
            TaskListItemDto(id = 3, parentId = 0, order = 2, level = Level.One), // to
            TaskListItemDto(id = 4, parentId = 0, order = 3, level = Level.One),
        )

        // Act
        val result = sut.reorderTasks(from, to, items)

        // Assert
        assert(result.isNotEmpty())
        assert(result.size == 4)
        assert(result[0].id == items[2].id && result[0].order == 0)
        assert(result[1].id == items[3].id && result[1].order == 1)
        assert(result[2].id == items[1].id && result[2].order == 2)
        assert(result[3].id == items[4].id && result[3].order == 3)
    }

    @Test
    fun reorderTasks_moveTaskWithDifferentAncestorDownAndBetweenSameLevelTasks_returnReorderedTasks() {
        // Arrange
        val from = 0
        val to = 3
        val items = listOf(
            TaskListItemDto(id = 0, order = 0, level = Level.Zero), // from
            TaskListItemDto(id = 1, order = 1, level = Level.Zero),
            TaskListItemDto(id = 2, parentId = 0, order = 0, level = Level.One),
            TaskListItemDto(id = 3, parentId = 0, order = 1, level = Level.One), // to
            TaskListItemDto(id = 4, parentId = 0, order = 2, level = Level.One),
            TaskListItemDto(id = 5, parentId = 0, order = 3, level = Level.One),
        )

        // Act
        val result = sut.reorderTasks(from, to, items)

        // Assert
        assert(result.isNotEmpty())
        assert(result.size == 5)
        assert(result[0].id == items[2].id && result[0].order == 0)
        assert(result[1].id == items[3].id && result[1].order == 1)
        assert(result[2].id == items[0].id && result[2].order == 2)
        assert(result[3].id == items[4].id && result[3].order == 3)
        assert(result[4].id == items[5].id && result[4].order == 4)
    }

    @Test
    fun reorderTasks_moveTaskDownAndBetweenLevelZeroAndOne_returnReorderedTasks() {
        // Arrange
        val from = 0
        val to = 1
        val items = listOf(
            TaskListItemDto(id = 0, order = 0, level = Level.Zero), // from
            TaskListItemDto(id = 1, order = 1, level = Level.Zero), // to
            TaskListItemDto(id = 2, parentId = 1, order = 0, level = Level.One),
            TaskListItemDto(id = 3, parentId = 1, order = 1, level = Level.One),
            TaskListItemDto(id = 4, order = 2, level = Level.Zero)
        )

        // Act
        val result = sut.reorderTasks(from, to, items)

        // Assert
        assert(result.isNotEmpty())
        assert(result.size == 5)
        assert(result.any { it.id == items[1].id && it.order == 0 })
        assert(result.any { it.id == items[0].id && it.order == 0 })
        assert(result.any { it.id == items[2].id && it.order == 1 })
        assert(result.any { it.id == items[3].id && it.order == 2 })
        assert(result.any { it.id == items[4].id && it.order == 1 })
    }
}