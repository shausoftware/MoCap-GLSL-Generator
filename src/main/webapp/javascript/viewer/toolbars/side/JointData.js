'use strict';

import React from 'react';
import { useEffect } from 'react';

const JointData = (props) => {

    const jointColours = ['white', 'red', 'green', 'blue', 'yellow'];

    let toolsRef = React.useRef();

    const closeToolbar = (e) => {
        props.openDialog('jointDialog');
    }

    const handleJointDisplayChange = (e) => {
        props.updateJoint(e.target.value, 'display', e.target.checked);
    }

    const handleJointColourChange = (e) => {
        props.updateJoint(e.target.id, 'colour', e.target.text);
    }

    //TODO:
    const handlePositionChange = (e) => {
        props.updateJoint(e.target.id, e.target.name, e.target.value);
    }

    const handleSetAsCenter = (e) => {
        props.setAsCenterJoint(e.target.id);
    }

    const loadColourOptions = (id, jointColour) => {
        return jointColours.map((colour) => {
            return(<li key={colour}>
                       <a className={jointColour == colour ? "dropdown-item active" : "dropdown-item"}
                           id={id}
                           onClick={handleJointColourChange}
                           href="#">{colour}</a>
                   </li>
            );
        });
    }

    const loadRows = () => {
        let rows;
        if (props.scene.frames.length > 0) {
            rows = props.scene.frames[props.jointDataFrame].joints.map((joint) => {
                let jointId = joint.id - 1;
                let centerButtonClass = (jointId == props.offsetJointId) ? "btn btn-success" : "btn btn-secondary";
                return( <tr key={jointId + '-' + joint.display}>
                            <td>{jointId}</td>
                            <td>
                                <input className="form-check-input"
                                       type="checkbox"
                                       value={jointId}
                                       checked={joint.display}
                                       onChange={handleJointDisplayChange}/>
                            </td>
                            <td>
                                <div className="dropdown">
                                    <button className="btn btn-secondary btn-sm dropdown-toggle" type="button" id="jointColour" data-bs-toggle="dropdown" aria-expanded="false">
                                        Joint Colour
                                    </button>
                                    <ul className="dropdown-menu" aria-labelledby="jointColour">
                                        {loadColourOptions(jointId, joint.colour)}
                                    </ul>
                                </div>
                            </td>
                            <td>
                                <button className={centerButtonClass} type="button" id={jointId} onClick={handleSetAsCenter}>
                                    Center
                                </button>
                            </td>
                            <td>
                                <input id={jointId} name="x" type="number" value={joint.x} onChange={handlePositionChange} />
                            </td>
                            <td>
                                <input id={jointId} name="y" type="number" value={joint.y} onChange={handlePositionChange} />
                            </td>
                            <td>
                                <input id={jointId} name="z" type="number" value={joint.z} onChange={handlePositionChange} />
                            </td>
                        </tr>
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
                            <th>Joint ID</th>
                            <th>Display</th>
                            <th>Colour</th>
                            <th>Center Joint</th>
                            <th>X</th>
                            <th>Y</th>
                            <th>Z</th>
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

export default JointData;