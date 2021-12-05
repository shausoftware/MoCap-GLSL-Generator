'use strict';

import React from 'react';
import { useEffect } from 'react';

const Fourier = (props) => {

    const scales = ["10.0", "5.0",  "2.0", "1.0", "0.5", "0.25", "0.1"];

    const [downloadFilename, setDownloadFilename] = React.useState("");
    const [fileDownloadUrl, setFileDownloadUrl] = React.useState("");
    const [generateFourierError, setGenerateFourierError] = React.useState(false);
    const [fourierScale, setFourierScale] = React.useState(props.playbackParameters.scale);
    const [fourierFrames, setFourierFrames] = React.useState(1);
    const [enableLowRes, setEnableLowRes] = React.useState(false);
    const [lowResStart, setLowResStart] = React.useState(0);
    const [fourierFramesError, setFourierFramesError] = React.useState(false);
    const [lowResStartError, setLowResStartError] = React.useState(false);

    let toolsRef = React.useRef();
    let fourierFramesHelpRef = React.useRef();
    let lowResStartHelpRef = React.useRef();
    let downloadRef = React.useRef();
    let errorRef = React.useRef();

    const maxFourierFrames = () => {
        return props.playbackParameters.endFrame - props.playbackParameters.startFrame;
    }

    const closeToolbar = (e) => {
        setFileDownloadUrl("");
        setDownloadFilename("");
        props.openDialog('fourierDialog');
    }

    const resetState = () => {
        setFourierFrames(maxFourierFrames());
        setEnableLowRes(false);
        setLowResStart(maxFourierFrames());
        setFourierScale(props.playbackParameters.scale);
        setFourierFramesError(false);
        setLowResStartError(false);
    }

    const generateFourier = async (data) => {
        const response = await fetch('/generateFourier', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });
        if (response.status !== 200) {
            throw new Error('Generate Fourier Error: ' + response.status);
        }
        return await response.text();
    }

    const handleSubmit = (e) => {
        e.preventDefault();
        let valid = true;
        if (!fourierFrames || fourierFrames > maxFourierFrames()) {
            valid = false;
            setFourierFramesError(true);
        }
        if (enableLowRes) {
            if (!lowResStart || lowResStart > fourierFrames) {
                valid = false;
                setLowResStartError(true);
            }
            if ((fourierFrames - lowResStart) % 2 != 0) {
                valid = false;
                setLowResStartError(true);
            }
        }
        if (valid) {
            let data = {'scene': props.scene,
                        'startFrame': props.playbackParameters.startFrame,
                        'endFrame': props.playbackParameters.endFrame,
                        'useEasing': props.playbackParameters.useLoopEasing,
                        'easingFrames': props.playbackParameters.loopEasingFrames,
                        'fourierScale': fourierScale,
                        'offset': props.offset,
                        'fourierFrames': fourierFrames,
                        'useLowRes': enableLowRes,
                        'lowResStartFrame': lowResStart};
            generateFourier(data).then(result => {
                setDownloadFilename(props.scene.filename.substring(0, props.scene.filename.indexOf(".")) + ".msh");
                var blob = new Blob([result], { type: "text/plain;charset=utf-8" });
                setFileDownloadUrl(URL.createObjectURL(blob));
            }).catch(e => setGenerateFourierError(true));
        }
    }

    const handleFourierFramesChange = (e) => {
        setFourierFrames(e.target.value);
        setFourierFramesError(false);
        setLowResStartError(false);
    }

    const handleEnableLowResChange = (e) => {
        setEnableLowRes(e.target.checked);
        setFourierFramesError(false);
        setLowResStartError(false);
    }

    const handleLowResStartChange = (e) => {
        setLowResStart(e.target.value);
        setFourierFramesError(false);
        setLowResStartError(false);
    }

    const handleScaleClick = (e) =>  {
        setFourierScale(e.target.text);
    }

    const loadScaleOptions = () => {
        return scales.map((scale) => {
            return(<li key={scale}>
                        <a className={fourierScale == scale ? "dropdown-item active" : "dropdown-item"}
                           onClick={handleScaleClick}
                           href="#">{scale}</a>
                   </li>);
        });
    }

    useEffect(() => {

        if (props.updateProps) {
            resetState();
            props.setUpdateProps(false);
        }

        if (fileDownloadUrl != "") {
            downloadRef.current.click();
            URL.revokeObjectURL(fileDownloadUrl);
            closeToolbar();
        }

        if (generateFourierError) {
            errorRef.current.style.display = 'block';
        } else {
            errorRef.current.style.display = 'none';
        }
        if (fourierFramesError) {
            fourierFramesHelpRef.current.className = "form-text text-danger";
        } else {
            fourierFramesHelpRef.current.className = "form-text";
        }
        if (lowResStartError) {
            lowResStartHelpRef.current.className = "form-text text-danger";
        } else {
            lowResStartHelpRef.current.className = "form-text";
        }

        if (props.showDialogState.fourierDialog) {
            toolsRef.current.className = 'modal fade show';
            toolsRef.current.style.display = "block";
        } else {
            toolsRef.current.className = 'modal fade';
            toolsRef.current.style.display = "none";
        }
    });


    return(
        <div ref={toolsRef} className="modal fade" id="fourierModal" tabIndex="-1" aria-labelledby="fourierModalLabel" aria-hidden="true">
            <div className="modal-dialog modal-dialog-centered">
               <div className="modal-content">
                    <div className="modal-header">
                        <h5 className="modal-title" id="fourierModalLabel">Generate Fourier</h5>
                        <button type="button" className="btn-close" data-bs-dismiss="modal" aria-label="Close" onClick={closeToolbar}></button>
                    </div>
                    <div className="modal-body">
                        <p>Generate and download a fourier transform of selected data for use in GLSL shaders. Using less fourier frames results
                            in greater compression. The output can be further compressed by encoding high frequency (less significant) fourier frames into 8 bits
                            (normally 16 bits).
                        </p>
                        <form onSubmit={handleSubmit}>
                            <div id="generateFourierError" ref={errorRef} className="form-text text-danger">Error processing fourier data</div>
                            <div className="mb-3">
                                <label className="form-label">
                                    Start Frame: {props.playbackParameters.startFrame} - End Frame: {props.playbackParameters.endFrame}
                                </label>
                                <label className="form-label">
                                    Use Easing: {props.playbackParameters.useLoopEasing ? "true"  : "false"} - Easing Frames: {props.playbackParameters.loopEasingFrames}
                                </label>
                            </div>
                            <div className="mb-3">
                                <label htmlFor="fourierFrames" className="form-label">Fourier Frames</label>
                                <input type="number" min="1" className="form-control" id="fourierFrames" aria-describedby="fourierFramesHelp" value={fourierFrames} onChange={handleFourierFramesChange}/>
                                <div id="fourierFramesHelp" ref={fourierFramesHelpRef} className="form-text">Expecting value between 0 and {maxFourierFrames()} </div>
                            </div>
                            <div className="mb-3">
                                <label htmlFor="scale" className="form-label">Fourier Scale</label>
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
                                <div className="row">
                                    <div className="col-1">
                                        <input type="checkbox" className="form-check-input" id="enableLowRes" checked={enableLowRes} onChange={handleEnableLowResChange}/>
                                    </div>
                                    <div className="col">
                                        <label htmlFor="enableLowRes" className="form-label">Enable 8 bit data for high frequency fourier data</label>
                                    </div>
                                </div>
                            </div>
                            <div className="mb-3">
                                <label htmlFor="lowResStart" className="form-label">Last Hi Res Frame</label>
                                <input type="number" min="0" className="form-control" id="lowResStart" aria-describedby="lowResStartHelp" value={lowResStart} onChange={handleLowResStartChange} disabled={!enableLowRes}/>
                                <div id="lowResStartHelp" ref={lowResStartHelpRef} className="form-text">
                                    Expecting value between 0 and {fourierFrames}. The difference between low res start frame and fourier frames
                                    must be divisible by 2.
                                </div>
                            </div>
                            <div className="mb-3">
                                <button type="submit" className="btn btn-secondary">Generate and Download Fourier</button>
                                <a className="invisible" download={downloadFilename} href={fileDownloadUrl} ref={downloadRef}>download data</a>
                            </div>
                        </form>
                    </div>
               </div>
            </div>
        </div>
    );
}

export default Fourier;