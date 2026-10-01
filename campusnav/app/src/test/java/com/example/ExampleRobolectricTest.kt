package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.CampusSeedData
import com.example.data.model.BuildingCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("CampusNav", appName)
    }

    @Test
    fun `verify campus seed buildings loaded`() {
        val buildings = CampusSeedData.buildings
        assertTrue("Campus should have seed buildings", buildings.isNotEmpty())
        assertTrue("Should contain academic buildings", buildings.any { it.category == BuildingCategory.ACADEMIC })
        assertTrue("Should contain library", buildings.any { it.category == BuildingCategory.LIBRARY })
    }

    @Test
    fun `verify pathway graph integrity`() {
        val nodes = CampusSeedData.pathNodes
        val edges = CampusSeedData.pathEdges
        assertTrue("Path graph must have nodes", nodes.size >= 10)
        assertTrue("Path graph must have edges", edges.isNotEmpty())

        // Ensure all edges connect existing nodes
        for (edge in edges) {
            val fromNode = nodes.find { it.id == edge.fromId }
            val toNode = nodes.find { it.id == edge.toId }
            assertNotNull("From node should exist: ${edge.fromId}", fromNode)
            assertNotNull("To node should exist: ${edge.toId}", toNode)
        }
    }
}
