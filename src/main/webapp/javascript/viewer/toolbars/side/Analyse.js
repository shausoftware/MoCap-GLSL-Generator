'use strict';

import React from 'react';
import { useEffect } from 'react';

const Analyse = (props) => {

    let toolsRef = React.useRef();

    const closeToolbar = (e) => {
        props.openDialog('analyseDialog');
    }

    const loadJointData = (joints) => {
        let jointData = joints.map((joint) => {
            if (joint.x == 0.0 && joint.y == 0.0 && joint.z == 0.0) {
                return '-';
            }
            return 'X';
        });
        return jointData;
    }

    const loadFrameData = () => {
        let frameData = props.scene.frames.map((frame) => {
            return(
                <tr key={frame.id}>
                    <td>{frame.id}</td>
                    <td>{frame.joints.length}</td>
                    <td>{loadJointData(frame.joints)}</td>
                </tr>
            );
        });
        return frameData;
    }

    useEffect(() => {
        if (props.showDialogState.analyseDialog) {
            toolsRef.current.className = 'offcanvas offcanvas-start show';
            toolsRef.current.style.visibility = "visible";
        } else {
            toolsRef.current.className = 'offcanvas offcanvas-start';
            toolsRef.current.style.visibility = "";
        }
    });

    return(

        <div ref={toolsRef} className="offcanvas offcanvas-start" tabIndex="-1" id="offcanvasAnalysis" aria-labelledby="offcanvasAnalysisLabel">
            <div className="offcanvas-header">
                <h5 className="offcanvas-title" id="offcanvasAnalysisLabel">Analysis</h5>
                <button type="button" className="btn-close text-reset" data-bs-dismiss="offcanvas" aria-label="Close" onClick={closeToolbar}></button>
            </div>
            <div className="offcanvas-body">
                <p> (X=Data) (-=No Data) </p>
                <table className="table">
                    <thead>
                        <tr>
                            <th>Frame</th>
                            <th>No. Joints</th>
                            <th>Joint Data</th>
                         </tr>
                    </thead>
                    <tbody>
                        {loadFrameData()}
                    </tbody>
                </table>
            </div>
        </div>
    );
}

export default Analyse;