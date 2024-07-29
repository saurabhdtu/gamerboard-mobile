package com.gamerboard.live.gamestatemachine.bgmi.stateMachine

import com.gamerboard.live.gamestatemachine.games.LabelUtils
import com.gamerboard.live.gamestatemachine.games.MachineLabelUtils
import com.gamerboard.live.gamestatemachine.stateMachine.*
import com.google.common.truth.Truth.assertThat
import com.tinder.StateMachine
import org.junit.After
import org.junit.Before
import org.junit.Test

class VisionStateMachineTest {

    private lateinit var visionStateMachine: StateMachine<VisionState, VisionEvent, VisionEffect>

    @Before
    fun setup() {
        visionStateMachine = VisionStateMachine.visionImageSaver
    }

    @Test
    fun test_vision_does_not_save_image_if_not_set_to_record_images() {
        assertThat(visionStateMachine.state is VisionState.NoVisionImages).isTrue()
        visionStateMachine.transition(
            VisionEvent.ReceivedKillsScreen(
                imageKills = "1_kills.jpg",
                arrayListOf()
            )
        )
        assertThat(visionStateMachine.state is VisionState.NoVisionImages).isTrue()
        assertThat((visionStateMachine.state as VisionParams).recordKills).isFalse()
        assertThat((visionStateMachine.state as VisionParams).recordRatings).isFalse()
    }

    @Test
    fun test_vision_starts_recording_on_event_for_start_received() {
        assertThat(visionStateMachine.state is VisionState.NoVisionImages).isTrue()

        // start recording
        visionStateMachine.transition(
            VisionEvent.StartRecordingVisionImages(
                gameId = "100",
                "Testing star vision record"
            )
        )

        visionStateMachine.transition(
            VisionEvent.ReceivedKillsScreen(
                imageKills = "1_kills.jpg",
                arrayListOf()
            )
        )
        assertThat((visionStateMachine.state as VisionImagesProvider).killsImage).isNotNull()
        assertThat((visionStateMachine.state as VisionImagesProvider).killsImage).isEqualTo("1_kills.jpg")

        // this is null as not received yet
        assertThat((visionStateMachine.state as VisionParams).ratingsImage).isNull()
        visionStateMachine.transition(
            VisionEvent.ReceivedRatingsScreen(
                imageRatings = "1_ratings.jpg",
                arrayListOf()
            )
        )

        // now it is there
        assertThat((visionStateMachine.state as VisionImagesProvider).ratingsImage).isNotNull()
        assertThat((visionStateMachine.state as VisionImagesProvider).ratingsImage).isEqualTo("1_ratings.jpg")
    }

    @Test
    fun test_vision_new_image_replaces_previous() {
        assertThat(visionStateMachine.state is VisionState.NoVisionImages).isTrue()

        // start recording
        visionStateMachine.transition(
            VisionEvent.StartRecordingVisionImages(
                gameId = "100",
                "Testing star vision record"
            )
        )

        visionStateMachine.transition(
            VisionEvent.ReceivedKillsScreen(
                imageKills = "1_kills.jpg",
                arrayListOf()
            )
        )
        visionStateMachine.transition(
            VisionEvent.ReceivedRatingsScreen(
                imageRatings = "1_ratings.jpg",
                arrayListOf()
            )
        )

        assertThat((visionStateMachine.state as VisionImagesProvider).killsImage).isEqualTo("1_kills.jpg")
        assertThat((visionStateMachine.state as VisionImagesProvider).ratingsImage).isEqualTo("1_ratings.jpg")

        visionStateMachine.transition(
            VisionEvent.ReceivedKillsScreen(
                imageKills = "2_kills.jpg",
                arrayListOf()
            )
        )
        visionStateMachine.transition(
            VisionEvent.ReceivedRatingsScreen(
                imageRatings = "2_ratings.jpg",
                arrayListOf()
            )
        )

        assertThat((visionStateMachine.state as VisionImagesProvider).killsImage).isEqualTo("2_kills.jpg")
        assertThat((visionStateMachine.state as VisionImagesProvider).ratingsImage).isEqualTo("2_ratings.jpg")
    }

    @Test
    fun test_image_does_not_update_after_specified_count_of_images() {
        assertThat(visionStateMachine.state is VisionState.NoVisionImages).isTrue()

        // start recording
        visionStateMachine.transition(
            VisionEvent.StartRecordingVisionImages(
                gameId = "100",
                "Testing star vision record"
            )
        )

        for (i in 0..(captureAutoMlImages * 2)) {
            visionStateMachine.transition(
                VisionEvent.ReceivedKillsScreen(
                    imageKills = "${i}_kills.jpg",
                    arrayListOf()
                )
            )
            visionStateMachine.transition(
                VisionEvent.ReceivedRatingsScreen(
                    imageRatings = "${i}_ratings.jpg",
                    arrayListOf()
                )
            )
        }

        assertThat((visionStateMachine.state as VisionImagesProvider).killsImage).isEqualTo("3_kills.jpg")
        assertThat((visionStateMachine.state as VisionImagesProvider).ratingsImage).isEqualTo("3_ratings.jpg")
    }

    @Test
    fun test_vision_images_for_alternating_rank_rating_screens(){
        assertThat(visionStateMachine.state is VisionState.NoVisionImages).isTrue()

        // start recording
        visionStateMachine.transition(
            VisionEvent.StartRecordingVisionImages(
                gameId = "100",
                "Testing star vision record"
            )
        )

        visionStateMachine.transition(
            VisionEvent.StartRecordingVisionImages(
                gameId = "100",
                "Testing star vision record"
            )
        )

        visionStateMachine.transition(
            VisionEvent.ReceivedKillsScreen(
                imageKills = "1_kills.jpg",
                arrayListOf()
            )
        )

        visionStateMachine.transition(
            VisionEvent.ReceivedKillsScreen(
                imageKills = "1_kills.jpg",
                arrayListOf()
            )
        )
        visionStateMachine.transition(
            VisionEvent.StartRecordingVisionImages(
                gameId = "100",
                "Testing star vision record"
            )
        )
        assertThat((visionStateMachine.state as VisionState.RecordImages).ready).isFalse()
        visionStateMachine.transition(
            VisionEvent.StartRecordingVisionImages(
                gameId = "100",
                "Testing star vision record"
            )
        )
        visionStateMachine.transition(
            VisionEvent.ReceivedRatingsScreen(
                imageRatings = "2_ratings.jpg",
                arrayListOf()
            )
        )

        assertThat((visionStateMachine.state as VisionState.RecordImages).ready).isTrue()
    }

    @After
    fun cleanUp() {
        LabelUtils.testLogGreen("Test passed!")
        visionStateMachine.transition(VisionEvent.ResetVision("Test: Reset on cleanup"))
    }
}