'use strict';

import React from 'react';
import { useEffect } from 'react';

import Joint from "./Joint";

const Joints = (props) => {

    let toolsRef = React.useRef();

    const closeToolbar = (e) => {
        props.openDialog('jointDialog');
    }

    const loadRows = () => {
        let rows;
        if (props.scene.frames.length > 0) {
            rows = props.scene.frames[props.jointDataFrame].joints.map((joint) => {
                return(
                    <Joint key={joint.id}
                           frameId={props.jointDataFrame}
                           joint={joint}
                           updateJoint={props.updateJoint}
                           updateProps={props.updateProps}
                           setUpdateProps={props.setUpdateProps}
                           />
                );
            });
        }
        return rows;
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
            <div className="offcanvas-header">
                <h5 className="offcanvas-title" id="offcanvasToolsLabel">Joint Data</h5>
                <button type="button" className="btn-close text-reset" data-bs-dismiss="offcanvas" aria-label="Close" onClick={closeToolbar}></button>
            </div>
            <div className="offcanvas-body">
                <p className="lead">Joints for Frame: {props.jointDataFrame}</p>
                <table className="table">
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