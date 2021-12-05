'use strict';

import React from 'react';
import { useEffect } from 'react';

const Help = (props) => {

    let toolsRef = React.useRef();

    const closeToolbar = (e) => {
        props.openDialog('helpDialog');
    }

    useEffect(() => {
        if (props.showDialogState.helpDialog) {
            toolsRef.current.className = 'offcanvas offcanvas-start show';
            toolsRef.current.style.visibility = "visible";
        } else {
            toolsRef.current.className = 'offcanvas offcanvas-start';
            toolsRef.current.style.visibility = "";
        }
    });

    return(

        <div ref={toolsRef} className="offcanvas offcanvas-start" tabIndex="-1" id="offcanvasHelp" aria-labelledby="offcanvasHelpLabel">
            <div className="offcanvas-header">
                <h5 className="offcanvas-title" id="offcanvasHelpLabel">MoCap GLSL Generator - version {props.apiVersion}</h5>
                <button type="button" className="btn-close text-reset" data-bs-dismiss="offcanvas" aria-label="Close" onClick={closeToolbar}></button>
            </div>
            <div className="offcanvas-body">
                <h6>Overview</h6>
                <p><small>
                    A tool for loading and editing well formed C3d & TRC motion capture data with the purpose of generating compressed GLSL suitable for applications such as Shadertoy.
                    See <em>https://github.com/shausoftware/MoCap-GLSL-Generator</em> for source code and sample projects.
                </small></p>
                <h6>Project Menu</h6>
                <p><small>
                    <strong>Import</strong> - import well formed C3d & TRC motion capture data into the editor/player. Options are available to re-assign x,y,z axis (the default assignment
                    is XZY).
                </small></p>
                <p><small>
                    <strong>Save</strong> - save the state of your current session (.mcd file) so that you can return to it later. Select Delete Hidden
                    Joints Option to permanently remove hidden joints from dataset (large datasets are currently slowing performance).
                </small></p>
                <p><small>
                    <strong>Open</strong> - open a previously saved project (.mcd file). Sample projects are available on the Github repository.
                </small></p>
                <h6>Stats</h6>
                <p><small>
                    Toggle playback statistics in player window.
                </small></p>
                <h6>Playback</h6>
                <p><small>
                    Play/Pause motion capture scene and set duration between each frame.
                </small></p>
                <h6>Tools Menu</h6>
                <p><small>
                    <strong>Playback Parameters</strong> - set the start frame, end frame, scale and view of the playback loop. Options are available to ease joints of the last <em>n</em> frames
                    of the loop to joints at the start frame of loop. This is still a bit of an experiment so it might not work too well. The start and end frame are passed to the Fourier
                    generation tool.
                </small></p>
                <p><small>
                    <strong>Offsets</strong> - use this to create offsets for joint data so that data can be centered in the viewer. The current offset state can be seen on this panel.
                    Manual XYZ offsets can be set or the current offset state can be cleared. Note: Joint offsets are set from the Joint Data panel (see below). This offset is passed to the
                    Fourier generation tool.
                </small></p>
                <p><small>
                    <strong>Joint Data</strong> - use this tool to set the display and colour of each joint globally. The XYZ position of each joint can be set for each frame.
                </small></p>
                <p><small>
                    <strong>Analyse</strong> - this displays metadata of the current MoCap scene and can be used to cull empty joint data when saving the project.
                </small></p>
                <p><small>
                    <strong>Fourier</strong> - use this tool to generate GLSL code suitable for Shadertoy. It allows for data compression using Fourier transforms and data encoding.
                    The tool uses the scaling, start and end frame data set in the player to determine the initial number of Fourier frames. This value should be reduced to a suitable
                    value for your animation to engage the Fourier compression. By default the Fourier transform is encoded into 16 bit integers but an option exists to encode the higher
                    frequency (less significant) fourier data into 8 bit integers. Check the debug data generated for potential scaling issues.
                </small></p>
            </div>
        </div>
    );
}

export default Help;