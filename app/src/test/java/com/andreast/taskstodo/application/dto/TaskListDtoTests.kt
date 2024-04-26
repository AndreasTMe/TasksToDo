package com.andreast.taskstodo.application.dto

import org.junit.Assert
import org.junit.Test

class TaskListDtoTests {
    @Test
    fun givenTaskListWithOneLevelOfItems_whenGetAllItemIdsCalled_thenOneIdIsReturned() {
        // Arrange
        val parentId: Long = 1
        val taskListDto = TaskListDto(
            items = mutableListOf(
                TaskListItemDto(id = parentId),
                TaskListItemDto(id = 2),
                TaskListItemDto(id = 3),
                TaskListItemDto(id = 4)
            )
        )

        // Act
        val result = taskListDto.getParentAndChildrenIds(parentId)

        // Assert
        Assert.assertEquals(1, result.size)
        Assert.assertEquals(parentId, result[0])
    }

    @Test
    fun givenTaskListWithOneLevelOfItems_whenGetAllItemIdsCalledAndNoMatch_thenEmptyListIsReturned() {
        // Arrange
        val parentId: Long = 69420
        val taskListDto = TaskListDto(
            items = mutableListOf(
                TaskListItemDto(id = 1),
                TaskListItemDto(id = 2),
                TaskListItemDto(id = 3),
                TaskListItemDto(id = 4)
            )
        )

        // Act
        val result = taskListDto.getParentAndChildrenIds(parentId)

        // Assert
        Assert.assertEquals(0, result.size)
    }

    @Test
    fun givenTaskListWithMultipleLevelsOfItems_whenGetAllItemIdsCalled_thenAllIdsAreReturned() {
        // Arrange
        val parentId: Long = 2
        val childId1: Long = 3
        val childId2: Long = 4
        val grandchildId: Long = 5

        val taskListDto = TaskListDto(
            items = mutableListOf(
                TaskListItemDto(
                    id = 1,
                    children = mutableListOf(
                        TaskListItemDto(
                            id = parentId,
                            children = mutableListOf(
                                TaskListItemDto(id = childId1),
                                TaskListItemDto(
                                    id = childId2,
                                    children = mutableListOf(
                                        TaskListItemDto(id = grandchildId)
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )

        // Act
        val result = taskListDto.getParentAndChildrenIds(parentId)

        // Assert
        Assert.assertEquals(4, result.size)
        Assert.assertEquals(parentId, result[0])
        Assert.assertEquals(childId1, result[1])
        Assert.assertEquals(childId2, result[2])
        Assert.assertEquals(grandchildId, result[3])
    }

    @Test
    fun givenTaskListWithOneLevelOfItems_whenRemoveItem_thenOneItemIsRemoved() {
        // Arrange
        val itemToRemove = TaskListItemDto(id = 1)
        val taskListDto = TaskListDto(
            items = mutableListOf(
                itemToRemove,
                TaskListItemDto(id = 2),
                TaskListItemDto(id = 3),
                TaskListItemDto(id = 4)
            )
        )

        // Act
        taskListDto.removeItem(itemToRemove)

        // Assert
        Assert.assertEquals(3, taskListDto.items.size)
        Assert.assertTrue(taskListDto.items.none { it.id == itemToRemove.id })
    }

    @Test
    fun givenTaskListWithOneLevelOfItems_whenRemoveItemThatDoesNotExist_thenNoChange() {
        // Arrange
        val itemToRemove = TaskListItemDto(id = 69420)
        val taskListDto = TaskListDto(
            items = mutableListOf(
                TaskListItemDto(id = 1),
                TaskListItemDto(id = 2),
                TaskListItemDto(id = 3),
                TaskListItemDto(id = 4)
            )
        )

        // Act
        taskListDto.removeItem(itemToRemove)

        // Assert
        Assert.assertEquals(4, taskListDto.items.size)
        Assert.assertTrue(taskListDto.items.none { it.id == itemToRemove.id })
    }

    @Test
    fun givenTaskListWithMultipleLevelsOfItems_whenRemoveItemWithChildren_thenItemAndChildrenRemoved() {
        // Arrange
        val itemToRemove = TaskListItemDto(
            id = 4,
            children = mutableListOf(
                TaskListItemDto(id = 5),
                TaskListItemDto(id = 6)
            )
        )

        val taskListDto = TaskListDto(
            items = mutableListOf(
                TaskListItemDto(
                    id = 1,
                    children = mutableListOf(
                        TaskListItemDto(
                            id = 2,
                            children = mutableListOf(
                                TaskListItemDto(id = 3),
                                itemToRemove
                            )
                        )
                    )
                )
            )
        )

        // Act
        taskListDto.removeItem(itemToRemove)

        // Assert
        Assert.assertEquals(1, taskListDto.items.size)
        Assert.assertNotEquals(itemToRemove.id, taskListDto.items[0].id)
        Assert.assertEquals(1, taskListDto.items[0].children.size)
        Assert.assertNotEquals(itemToRemove.id, taskListDto.items[0].children[0].id)
        Assert.assertEquals(1, taskListDto.items[0].children[0].children.size)
        Assert.assertNotEquals(itemToRemove.id, taskListDto.items[0].children[0].children[0].id)
    }
}