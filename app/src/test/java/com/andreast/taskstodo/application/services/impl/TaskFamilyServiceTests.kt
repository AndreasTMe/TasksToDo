package com.andreast.taskstodo.application.services.impl

import com.andreast.taskstodo.application.dto.TaskListItemDto
import com.andreast.taskstodo.application.utils.Level
import org.junit.Test

class TaskFamilyServiceTests {

    private val sut = TaskFamilyService()

    @Test
    fun getParentAndChildren_parentIdDoesNotExist_shouldReturnEmptyList() {
        // Arrange
        val parentId: Long = 123
        val items = listOf(
            TaskListItemDto(id = 0),
            TaskListItemDto(id = 1),
            TaskListItemDto(id = 2),
        )

        // Act
        val result = sut.getParentAndDescendants(parentId, items)

        // Assert
        assert(result.isEmpty())
    }

    @Test
    fun getParentAndChildren_itemsListIsEmpty_shouldReturnEmptyList() {
        // Arrange
        val parentId: Long = 123
        val items = listOf<TaskListItemDto>()

        // Act
        val result = sut.getParentAndDescendants(parentId, items)

        // Assert
        assert(result.isEmpty())
    }

    @Test
    fun getParentAndChildren_itemsListContainsParentOnly_shouldReturnListWithParentItem() {
        // Arrange
        val parentId: Long = 1
        val items = listOf(
            TaskListItemDto(id = 0),
            TaskListItemDto(id = parentId), // Parent
            TaskListItemDto(id = 2),
        )

        // Act
        val result = sut.getParentAndDescendants(parentId, items)

        // Assert
        assert(result.isNotEmpty())
        assert(result.size == 1)
        assert(result[0].id == parentId)
    }

    @Test
    fun getParentAndChildren_itemsListContainsParentAndChildren_shouldReturnListWithParentAndChildren() {
        // Arrange
        val parentId: Long = 1
        val items = listOf(
            TaskListItemDto(id = 0),
            TaskListItemDto(id = parentId), // Parent
            TaskListItemDto(id = 2),
            TaskListItemDto(id = 3, parentId = parentId), // Child
            TaskListItemDto(id = 4, parentId = 3), // Grandchild
        )

        // Act
        val result = sut.getParentAndDescendants(parentId, items)

        // Assert
        assert(result.isNotEmpty())
        assert(result.size == 3)
        assert(result[0].id == parentId)
        assert(result[1].id == items[3].id)
        assert(result[2].id == items[4].id)
    }

    @Test
    fun getParentAndChildrenIds_parentIdDoesNotExist_shouldReturnEmptyList() {
        // Arrange
        val parentId: Long = 123
        val items = listOf(
            TaskListItemDto(id = 0),
            TaskListItemDto(id = 1),
            TaskListItemDto(id = 2),
        )

        // Act
        val result = sut.getParentAndDescendantsIds(parentId, items)

        // Assert
        assert(result.isEmpty())
    }

    @Test
    fun getParentAndChildrenIds_itemsListIsEmpty_shouldReturnEmptyList() {
        // Arrange
        val parentId: Long = 123
        val items = listOf<TaskListItemDto>()

        // Act
        val result = sut.getParentAndDescendantsIds(parentId, items)

        // Assert
        assert(result.isEmpty())
    }

    @Test
    fun getParentAndChildrenIds_itemsListContainsParentIdOnly_shouldReturnListWithParentItem() {
        // Arrange
        val parentId: Long = 1
        val items = listOf(
            TaskListItemDto(id = 0),
            TaskListItemDto(id = parentId), // Parent
            TaskListItemDto(id = 2),
        )

        // Act
        val result = sut.getParentAndDescendantsIds(parentId, items)

        // Assert
        assert(result.isNotEmpty())
        assert(result.size == 1)
        assert(result[0] == parentId)
    }

    @Test
    fun getParentAndChildrenIds_itemsListContainsParentAndChildrenIds_shouldReturnListWithParentAndChildren() {
        // Arrange
        val parentId: Long = 1
        val items = listOf(
            TaskListItemDto(id = 0),
            TaskListItemDto(id = parentId), // Parent
            TaskListItemDto(id = 2),
            TaskListItemDto(id = 3, parentId = parentId), // Child
            TaskListItemDto(id = 4, parentId = 3), // Grandchild
        )

        // Act
        val result = sut.getParentAndDescendantsIds(parentId, items)

        // Assert
        assert(result.isNotEmpty())
        assert(result.size == 3)
        assert(result[0] == parentId)
        assert(result[1] == items[3].id)
        assert(result[2] == items[4].id)
    }

    @Test
    fun getAncestors_itemsListContainsId_shouldReturnAncestors() {
        // Arrange
        val id: Long = 4
        val items = listOf(
            TaskListItemDto(id = 0, level = Level.Zero),
            TaskListItemDto(id = 1, level = Level.Zero),
            TaskListItemDto(id = 2, level = Level.Zero),
            TaskListItemDto(id = 3, parentId = 2, level = Level.One),
            TaskListItemDto(id = id, parentId = 3, level = Level.Two),
        )

        // Act
        val result = sut.getAncestors(id, items)

        // Assert
        assert(result.isNotEmpty())
        assert(result.size == 2)
        assert(result[0].id == items[3].id)
        assert(result[1].id == items[2].id)
    }

    @Test
    fun getAncestors_itemsListDoesNotContainId_shouldReturnEmptyList() {
        // Arrange
        val id: Long = 69
        val items = listOf(
            TaskListItemDto(id = 0, level = Level.Zero),
            TaskListItemDto(id = 1, level = Level.Zero),
            TaskListItemDto(id = 2, level = Level.Zero),
            TaskListItemDto(id = 3, parentId = 2, level = Level.One),
            TaskListItemDto(id = 4, parentId = 3, level = Level.Two),
        )

        // Act
        val result = sut.getAncestors(id, items)

        // Assert
        assert(result.isEmpty())
    }

    @Test
    fun getDescendants_itemsListContainsId_shouldReturnDescendants() {
        // Arrange
        val id: Long = 2
        val items = listOf(
            TaskListItemDto(id = 0),
            TaskListItemDto(id = 1),
            TaskListItemDto(id = id),
            TaskListItemDto(id = 3, parentId = id),
            TaskListItemDto(id = 4, parentId = 3),
            TaskListItemDto(id = 5, parentId = 3),
        )

        // Act
        val result = sut.getDescendants(id, items)

        // Assert
        assert(result.isNotEmpty())
        assert(result.size == 3)
        assert(result[0].id == items[3].id)
        assert(result[1].id == items[4].id)
        assert(result[2].id == items[5].id)
    }

    @Test
    fun getDescendants_itemsListDoesNotContainId_shouldReturnEmptyList() {
        // Arrange
        val id: Long = 69
        val items = listOf(
            TaskListItemDto(id = 0),
            TaskListItemDto(id = 1),
            TaskListItemDto(id = 2),
            TaskListItemDto(id = 3, parentId = 2),
            TaskListItemDto(id = 4, parentId = 3),
            TaskListItemDto(id = 5, parentId = 3),
        )

        // Act
        val result = sut.getDescendants(id, items)

        // Assert
        assert(result.isEmpty())
    }

    @Test
    fun getSiblings_itemsListContainsItem_shouldReturnSiblings() {
        // Arrange
        val id: Long = 3
        val items = listOf(
            TaskListItemDto(id = 0),
            TaskListItemDto(id = 1),
            TaskListItemDto(id = 2),
            TaskListItemDto(id = id, parentId = 2),
            TaskListItemDto(id = 4, parentId = 3),
            TaskListItemDto(id = 5, parentId = 2),
            TaskListItemDto(id = 6, parentId = 2),
        )

        // Act
        val result = sut.getSiblings(items[3], items)

        // Assert
        assert(result.isNotEmpty())
        assert(result.size == 2)
        assert(result[0].id == items[5].id)
        assert(result[1].id == items[6].id)
    }
}