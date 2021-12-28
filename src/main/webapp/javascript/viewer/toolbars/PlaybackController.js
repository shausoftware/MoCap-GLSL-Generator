'use strict';

import React from 'react';
import { useEffect } from 'react';

const PlaybackController = (props) => {

    const frameDurations = ["16", "32",  "64", "128", "256"];

    const handlePlayClick = (e) => {
        props.updatePlaying();
    }

    const handleFrameDurationClick = (e) =>  {
        props.updatePlaybackParameters(['frameDuration'], [e.target.text]);
    }

    const handleCurrentFrameChange = (e) => {
        e.preventDefault();
        props.updateCurrentFrame(e.target.value);
    }

    const handleViewParametersClick = (e) => {
        props.openDialog('viewDialog');
    }

    const handleOffsetDataClick = (e) => {
        props.openDialog('offsetDialog');
    }

    const handleJointDataClick = (e) =>  {
        props.openJointDialog();
    }

    const handleAxisClick = (e) => {
        props.openDialog('axisDialog');
    }

    const handleOpenProjectClick = (e) => {
        props.openDialog('openProjectDialog');
    }

    const handleSaveProjectClick = (e) => {
        props.openDialog('saveProjectDialog');
    }

    const handleImportClick = (e) => {
        props.openDialog('importDialog');
    }

    const handleAnalyseClick = (e) => {
        props.openDialog('analyseDialog');
    }

    const handleFourierClick = (e) => {
        props.openDialog('fourierDialog');
    }

    const handleShowStats = (e) => {
        props.openStats();
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

    const loadMenuClass = () => {
        return props.sceneLoaded ? "dropdown-item" : "dropdown-item disabled";
    }

    return (
        <div className="container-fluid p-0">
            <div className="btn-toolbar justify-content-sm-center">
                <div className="btn-group" role="group">
                    <button type="button" className="btn btn-secondary btn-sm" onClick={handlePlayClick} disabled={!props.sceneLoaded}>{props.playing ? "Pause" : "Play"}</button>
                </div>
                <div className="input-group">
                    <input type="number" min="0" id="currentFrame" className="form-control form-control-sm" value={props.currentFrame} onChange={handleCurrentFrameChange} disabled={!props.sceneLoaded}/>
                </div>
                <div className="btn-group" role="group">
                    <div className="dropdown">
                        <button className="btn btn-secondary btn-sm dropdown-toggle" type="button" id="frameDuration" data-bs-toggle="dropdown" aria-expanded="false" disabled={!props.sceneLoaded}>
                            Frame  Duration (ms)
                        </button>
                        <ul className="dropdown-menu" aria-labelledby="frameDuration">
                            {loadFrameDurationOptions()}
                        </ul>
                    </div>
                    <div className="dropdown">
                        <button className="btn btn-secondary btn-sm dropdown-toggle" type="button" id="tools" data-bs-toggle="dropdown" aria-expanded="false" disabled={!props.sceneLoaded}>
                            Tools
                        </button>
                        <ul className="dropdown-menu" aria-labelledby="tools">
                            <li>
                                <a className="dropdown-item" onClick={handleViewParametersClick}>
                                    View Parameters
                                </a>
                            </li>
                            <li>
                                <a className="dropdown-item" onClick={handleOffsetDataClick}>
                                    Offsets
                                </a>
                            </li>
                            <li>
                                <a className="dropdown-item" onClick={handleJointDataClick}>
                                    Joint Data
                                </a>
                            </li>
                            <li>
                                <a className="dropdown-item" onClick={handleAnalyseClick}>
                                    Analyse
                                </a>
                            </li>
                            <li>
                                <a className="dropdown-item" onClick={handleFourierClick}>
                                    Fourier
                                </a>
                            </li>
                        </ul>
                    </div>
                    <div className="dropdown">
                        <button className="btn btn-secondary btn-sm dropdown-toggle" type="button" id="project" data-bs-toggle="dropdown" aria-expanded="false">
                            Project
                        </button>
                        <ul className="dropdown-menu" aria-labelledby="project">
                            <li>
                                <a className="dropdown-item" onClick={handleOpenProjectClick}>
                                    Open Project
                                </a>
                            </li>
                            <li>
                                <a className={loadMenuClass()} onClick={handleSaveProjectClick}>
                                    Save Project
                                </a>
                            </li>
                            <li>
                                <a className="dropdown-item" onClick={handleImportClick}>
                                    Import MoCap Data
                                </a>
                            </li>
                        </ul>
                    </div>
                    <button type="button" className="btn btn-secondary btn-sm" onClick={handleShowStats}>Stats</button>
                </div>
            </div>
        </div>
    );
}

export default PlaybackController;