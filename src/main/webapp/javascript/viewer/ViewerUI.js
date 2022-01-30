'use strict';

import React from "react";
import { useEffect } from 'react';

import PlayController from "./toolbars/PlayController";
import Viewer from "./Viewer";

const ViewerUI = (props) => {

    const [currentFrame, setCurrentFrame] = React.useState(0);
    const [playing, setPlaying] = React.useState(false);

    const updatePlaying = () => {
        setPlaying(!playing);
    }

    const updateCurrentFrame = (frame) => {
        if (frame && !isNaN(frame) && frame >= 0 && frame < props.scene.frames.length) {
            setCurrentFrame(parseInt(frame));
        }
    }

    useEffect(() => {
        let interval = null;
        if (playing) {
            interval = setInterval(() => {
                updateCurrentFrame(currentFrame < props.playbackParameters.endFrame - 1 ? currentFrame + 1 : parseInt(props.playbackParameters.startFrame));
            }, props.playbackParameters.frameDuration);
        } else if (!playing) {
            clearInterval(interval);
        }
        return () => clearInterval(interval);
    }, [playing, currentFrame]);

    return (
        <div className="container-fluid h-100 w-100 p-0 bg-dark">
            <PlayController sceneLoaded={props.scene.frames.length > 0}
                            playing={playing}
                            updatePlaying={updatePlaying}
                            currentFrame={currentFrame}
                            updateCurrentFrame={updateCurrentFrame}
                            playbackParameters={props.playbackParameters}
                            updatePlaybackParameters={props.updatePlaybackParameters}
                            />
            <Viewer scene={props.scene}
                    playbackParameters={props.playbackParameters}
                    currentFrame={currentFrame}
                    offset={props.offset}
                    totalFrames={props.scene.frames.length}
                    showStats={props.showStats}
                    />
        </div>
    );
}

export default ViewerUI;