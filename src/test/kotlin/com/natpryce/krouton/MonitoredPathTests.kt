package com.natpryce.krouton

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.minutest.rootContext
import org.junit.platform.commons.annotation.Testable


@Testable
fun `reporting the monitored path`() = rootContext {
    test("reports symbolic name of variable path element") {
        assertThat(string.named("s").monitoredPath("foo"), equalTo("/{s}"))
        assertThat(int.named("i").monitoredPath(10), equalTo("/{i}"))
    }
    
    test("reports monitored values") {
        assertThat(string.named("s").monitored().monitoredPath("foo"), equalTo("/foo"))
        assertThat(int.named("s").monitored().monitoredPath(10), equalTo("/10"))
    }
    
    test("monitored path reporting with composition") {
        assertThat((string.named("planet") / double.named("lat") / double.named("lon")).monitoredPath("mars", 20.0, 50.5),
            equalTo("/{planet}/{lat}/{lon}"))
        
        assertThat((string.named("planet").monitored() / double.named("lat") / double.named("lon")).monitoredPath("earth", 19.3, 33.5),
            equalTo("/earth/{lat}/{lon}"))
    }
    
    test("monitored path reporting with composition with Empty") {
        assertThat((ROOT / string.named("planet")).monitoredPath("earth"), equalTo("/{planet}"))
        assertThat((ROOT / "planet" / string.named("planet") / "orbit").monitoredPath("mercury"), equalTo("/planet/{planet}/orbit"))
    }
    
    test("monitored path reporting with restriction") {
        val path = int.named("x") where { it > 0 }
        
        assertThat(path.monitoredPath(10), equalTo("/{x}"))
    }
    
    test("monitored path with projection") {
        val planetaryPosition = string.named("planet").monitored() / (double.named("lat") / double.named("lon") asA position)
        
        assertThat(planetaryPosition.monitoredPath("venus", Position(35.2, 16.4)), equalTo("/venus/{lat}/{lon}"))
    }
}

private data class Position(val x: Double, val y: Double)

private val position = projection<Tuple2<Double, Double>, Position>(
    fromParts = { Position(it.val1, it.val2) },
    toParts = { tuple(it.x, it.y) }
)
