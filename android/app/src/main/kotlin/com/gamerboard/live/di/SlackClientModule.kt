package com.gamerboard.live.di

import com.gamerboard.live.slack.SlackClient
import org.koin.dsl.module

val slackModule = module{
    single<SlackClient>{
        SlackClient()
    }
}