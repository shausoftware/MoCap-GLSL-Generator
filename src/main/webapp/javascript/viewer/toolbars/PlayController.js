'use strict'

import React from "react";

const PlayController = (props) => {

    const frameDurations = ["16", "32",  "64", "128", "256"];

    const handlePlayClick = (e) => {
        props.updatePlaying();
    }

    const handleFrameDurationClick = (e) =>  {
        props.updatePlaybackParameters(['frameDuration'], [e.target.text]);
    }

    const handleCurrentFrameChange = (e) => {
        e.preventDefault();
        let frame = e.target.value;
        if (frame && !isNaN(frame)) {
            props.updateCurrentFrame(frame);
        }
    }

    const loadFrameDurationOptions = () => {
        return frameDurations.map((duration) => {
            return(<li key={duration}>
                <a className={props.playbackParameters.frameDuration == duration ? "dropdown-item active" : "dropdown-item"}
                   onClick={handleFrameDurationClick}
                   href="#">{duration}</a>
            </li>);
        });
    }

    return (
        <div className="container-fluid p-0">
            <div className="btn-toolbar justify-content-center border border-dark">
                <div className="btn-group" role="group">
                    <button type="button" className="btn btn-secondary btn-sm" onClick={handlePlayClick} disabled={!props.sceneLoaded}>{props.playing ? "Pause" : "Play"}</button>
                </div>
                <div className="input-group">
                    <input type="number" min="0" id="currentFrame" className="form-control form-control-sm" value={props.currentFrame} onChange={handleCurrentFrameChange} disabled={!props.sceneLoaded}/>
                </div>
                <div className="dropdown">
                    <button className="btn btn-secondary btn-sm dropdown-toggle" type="button" id="frameDuration" data-bs-toggle="dropdown" aria-expanded="false" disabled={!props.sceneLoaded}>
                        Frame  Duration (ms)
                    </button>
                    <ul className="dropdown-menu" aria-labelledby="frameDuration">
                        {loadFrameDurationOptions()}
                    </ul>
                </div>
            </div>
        </div>
    );
}

export default PlayController;