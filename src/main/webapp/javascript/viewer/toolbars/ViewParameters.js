'use strict';

import React from 'react';
import { useEffect } from 'react';

const ViewParameters = (props) => {

    const scales = ["10.0", "5.0",  "2.0", "1.0", "0.5", "0.25", "0.1"];
    const views = ["XY", "YZ",  "XZ"];

    const [startFrame, setStartFrame] = React.useState(props.playbackParameters.startFrame);
    const [endFrame, setEndFrame] = React.useState(props.playbackParameters.endFrame);
    const [startFrameError, setStartFrameError] = React.useState(false);
    const [endFrameError, setEndFrameError] = React.useState(false);
    const [useLoopEasing, setUseLoopEasing] = React.useState(props.playbackParameters.useLoopEasing);
    const [loopEasingFrames, setLoopEasingFrames] = React.useState(props.playbackParameters.loopEasingFrames);
    const [loopEasingFramesError, setLoopEasingFramesError] = React.useState(false);

    let toolsRef = React.useRef();
    let startFrameHelpRef = React.useRef();
    let endFrameHelpRef = React.useRef();
    let easingFramesHelpRef = React.useRef();

    const closeToolbar = (e) => {
        props.openDialog('viewDialog');
    }

    const resetState = () => {
        setStartFrame(props.playbackParameters.startFrame);
        setEndFrame(props.playbackParameters.endFrame);
        setStartFrameError(false);
        setEndFrameError(false);
        //TODO: implement
        //setUseLoopEasing(props.playbackParameters.useLoopEasing);
        setUseLoopEasing(false);
        setLoopEasingFrames(props.playbackParameters.loopEasingFrames);
        setLoopEasingFramesError(false);
    }

    const handleViewParametersFormSubmit = (e) => {
        e.preventDefault();
        let valid = true;
        if (!(parseInt(startFrame) >= 0 && parseInt(startFrame) < parseInt(endFrame))) {
            valid = false;
            setStartFrameError(true);
        } else if (!(parseInt(endFrame) > parseInt(startFrame) && parseInt(endFrame) <= parseInt(props.totalFrames))) {
            valid = false;
            setEndFrameError(true);
        }
        if (useLoopEasing && (!loopEasingFrames || loopEasingFrames > (endFrame - startFrame))) {
            valid = false;
            setLoopEasingFramesError(true);
        }
        if (valid)  {
            props.updatePlaybackParameters(['startFrame', 'endFrame', 'useLoopEasing', 'loopEasingFrames'],
                                           [startFrame, endFrame, useLoopEasing, loopEasingFrames]);
        }
    }

    const handleStartFrameChange = (e) => {
        setStartFrame(e.target.value);
        setStartFrameError(false);
        setEndFrameError(false);
        setLoopEasingFramesError(false);
    }

    const handleEndFrameChange = (e) => {
        setEndFrame(e.target.value);
        setStartFrameError(false);
        setEndFrameError(false);
        setLoopEasingFramesError(false);
    }

    const handleUseLoopEasingChange = (e) => {
        setUseLoopEasing(e.target.checked);
    }

    const handleEasingFramesChange = (e) => {
        setLoopEasingFrames(e.target.value);
        setStartFrameError(false);
        setEndFrameError(false);
        setLoopEasingFramesError(false);
    }

    const handleScaleClick = (e) =>  {
        props.updatePlaybackParameters(['scale'],[e.target.text]);
    }

    const handleViewClick = (e) => {
        props.updatePlaybackParameters(['view'],[e.target.text]);
    }

    const loadScaleOptions = () => {
        return scales.map((scale) => {
            return(<li key={scale}>
                        <a className={props.playbackParameters.scale == scale ? "dropdown-item active" : "dropdown-item"}
                           onClick={handleScaleClick}
                           href="#">{scale}</a>
                   </li>);
        });
    }

    const loadViewOptions = () => {
        return views.map((view) => {
            return(<li key={view}>
                        <a className={props.playbackParameters.view == view ? "dropdown-item active" : "dropdown-item"}
                           onClick={handleViewClick}
                           href="#">{view}</a>
                   </li>);
        });
    }

    useEffect(() => {

        if (props.updateProps) {
            resetState();
            props.setUpdateProps(false);
        }

        if (startFrameError) {
            startFrameHelpRef.current.className = "form-text text-danger";
        } else {
            startFrameHelpRef.current.className = "form-text";
        }
        if (endFrameError) {
            endFrameHelpRef.current.className = "form-text text-danger";
        } else {
            endFrameHelpRef.current.className = "form-text";
        }
        if (loopEasingFramesError) {
            easingFramesHelpRef.current.className = "form-text text-danger";
        } else {
            easingFramesHelpRef.current.className = "form-text";
        }

        if (props.showDialogState.viewDialog) {
            toolsRef.current.className = 'offcanvas offcanvas-start show';
            toolsRef.current.style.visibility = "visible";
        } else {
            toolsRef.current.className = 'offcanvas offcanvas-start';
            toolsRef.current.style.visibility = "";
        }
    });

    return (
        <div ref={toolsRef} className="offcanvas offcanvas-start" tabIndex="-1" id="viewParameters" aria-labelledby="viewParametersLabel">
            <div className="offcanvas-header bg-dark text-white">
                <h5 className="offcanvas-title" id="viewParametersLabel">View Parameters</h5>
                <button type="button" className="btn-close  bg-light" data-bs-dismiss="offcanvas" aria-label="Close" onClick={closeToolbar}></button>
            </div>
            <div className="offcanvas-body bg-dark text-white">
                <form onSubmit={handleViewParametersFormSubmit}>
                    <fieldset>
                        <div className="mb-3">
                            <label htmlFor="startFrame" className="form-label">Start Frame</label>
                            <input type="number" min="0" className="form-control" id="startFrame" aria-describedby="startFrameHelp" value={startFrame} onChange={handleStartFrameChange}/>
                            <div id="startFrameHelp" ref={startFrameHelpRef} className="form-text">Expecting a start frame between 0 and {parseInt(endFrame) - 1}</div>
                        </div>
                        <div className="mb-3">
                            <label htmlFor="endFrame" className="form-label">End Frame</label>
                            <input type="number" min="1" className="form-control" id="endFrame" aria-describedby="endFrameHelp" value={endFrame} onChange={handleEndFrameChange}/>
                            <div id="endFrameHelp" ref={endFrameHelpRef} className="form-text">Expecting an end frame between {parseInt(startFrame) + 1} and {props.totalFrames}</div>
                        </div>
                        <div className="mb-3">
                            <div className="row">
                                <div className="col-1">
                                    <input type="checkbox" className="form-check-input" id="useLoopEasing" checked={useLoopEasing} onChange={handleUseLoopEasingChange} disabled={true}/>
                                </div>
                                <div className="col">
                                    <label htmlFor="useLoopEasing" className="form-label">Use Loop Easing</label>
                                </div>
                            </div>
                        </div>
                        <div className="mb-3">
                            <label htmlFor="easingFrames" className="form-label">Easing Frames</label>
                            <input type="number" min="0" className="form-control" id="easingFrames" aria-describedby="easingFramesHelp" value={loopEasingFrames} onChange={handleEasingFramesChange} disabled={!useLoopEasing}/>
                            <div id="easingFramesHelp" ref={easingFramesHelpRef} className="form-text">
                                Expecting value between 0 and {endFrame - startFrame}. Eases joint positions between specified last n frames
                                of loop and start frame. Not currently implemented.
                            </div>
                        </div>
                    </fieldset>
                    <div className="mb-3">
                        <button type="submit" className="btn btn-secondary">Update Loop Parameters</button>
                    </div>
                </form>
                <div className="mb-3">
                    <label htmlFor="scale" className="form-label">Scale</label>
                    <div className="dropdown">
                        <button className="btn btn-secondary dropdown-toggle" type="button" id="scale" data-bs-toggle="dropdown" aria-expanded="false">
                            Scale
                        </button>
                        <ul className="dropdown-menu" aria-labelledby="scale">
                            {loadScaleOptions()}
                        </ul>
                    </div>
                </div>
                <div className="mb-3">
                    <label htmlFor="view" className="form-label">View</label>
                    <div className="dropdown">
                        <button className="btn btn-secondary dropdown-toggle" type="button" id="view" data-bs-toggle="dropdown" aria-expanded="false">
                            View
                        </button>
                        <ul className="dropdown-menu" aria-labelledby="view">
                            {loadViewOptions()}
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default ViewParameters;