'use strict';

import React from 'react';
import { useEffect } from 'react';

import Joint from "./Joint";

const Joints = (props) => {

    const [frameId, setFrameId] = React.useState(0);
    const [updateJoints, setUpdateJoints] = React.useState(false);

    let toolsRef = React.useRef();

    const closeToolbar = (e) => {
        props.openDialog('jointDialog');
    }

    const handleFrameIdClick = (e) => {
        setUpdateJoints(true);
        setFrameId(e.target.text);
    }

    const loadRows = () => {
        let rows;
        if (props.scene.frames.length > 0) {
            rows = props.scene.frames[frameId].joints.map((joint) => {
                return(
                    <Joint key={joint.id}
                           frameId={frameId}
                           joint={joint}
                           updateJoint={props.updateJoint}
                           updateProps={props.updateProps}
                           setUpdateProps={props.setUpdateProps}
                           updateJoints={updateJoints}
                           setUpdateJoints={setUpdateJoints}
                           />
                );
            });
        }
        return rows;
    }

    const frameIdOptions = () => {
        let options = [];
        if (props.scene.frames.length > 0) {
            props.scene.frames.map((frame) => {
                let fid = frame.id - 1; //frame ids are 1 based
                options.push(
                    <li key={fid}>
                        <a className={frameId == fid ? "dropdown-item active" : "dropdown-item"}
                           onClick={handleFrameIdClick}
                           href="#">{fid}</a>
                    </li>);
            });
        }
        return options;
    }

    useEffect(() => {
        if (props.showDialogState.jointDialog) {
            toolsRef.current.className = 'offcanvas offcanvas-start show';
            toolsRef.current.style.visibility = "visible";
        } else {
            toolsRef.current.className = 'offcanvas offcanvas-start';
            toolsRef.current.style.visibility = "";
        }
    });

    return (
        <div ref={toolsRef} className="offcanvas offcanvas-start" tabIndex="-1" id="offcanvasLoopParameters" aria-labelledby="offcanvasToolsLabel">
            <div className="offcanvas-header bg-dark text-white">
                <h5 className="offcanvas-title" id="offcanvasToolsLabel">Joint Data</h5>
                <button type="button" className="btn-close text-reset bg-light" data-bs-dismiss="offcanvas" aria-label="Close" onClick={closeToolbar}></button>
            </div>
            <div className="offcanvas-body bg-dark text-white">
                <p className="lead">Joints for Frame: {frameId}</p>
                <div className="mb-3">
                    <div className="dropdown">
                        <button className="btn btn-secondary btn-sm dropdown-toggle" type="button" id="frameId" data-bs-toggle="dropdown" aria-expanded="false">
                            Current Frame ID
                        </button>
                        <ul className="dropdown-menu scrollable-menu" aria-labelledby="assignX">
                            {frameIdOptions()}
                        </ul>
                    </div>
                </div>
                <table className="table text-white">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th></th>
                            <th>Display</th>
                            <th>Colour</th>
                            <th>X</th>
                            <th>GlobalX</th>
                            <th>Y</th>
                            <th>GlobalY</th>
                            <th>Z</th>
                            <th>GlobalZ</th>
                        </tr>
                    </thead>
                    <tbody>
                        {loadRows()}
                    </tbody>
                </table>
            </div>
        </div>
    );
}

export default Joints;