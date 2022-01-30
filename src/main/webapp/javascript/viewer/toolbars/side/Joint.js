'use strict';

import React from 'react';
import { useEffect } from 'react';

const Joint = (props) => {

    const GLOBAL_X = "globalX";
    const GLOBAL_Y = "globalY";
    const GLOBAL_Z = "globalZ";
    const jointColours = ['white', 'red', 'green', 'blue', 'yellow'];

    const [display, setDisplay] = React.useState(true);
    const [colour, setColour] = React.useState(jointColours[0]);
    const [x, setX] = React.useState(0.0);
    const [errorX, setErrorX] = React.useState(false);
    const [globalX, setGlobalX] = React.useState(false);
    const [y, setY] = React.useState(0.0);
    const [errorY, setErrorY] = React.useState(false);
    const [globalY, setGlobalY] = React.useState(false);
    const [z, setZ] = React.useState(0.0);
    const [errorZ, setErrorZ] = React.useState(false);
    const [globalZ, setGlobalZ] = React.useState(false);

    let errorXRef = React.useRef();
    let errorYRef = React.useRef();
    let errorZRef = React.useRef();

    let jointId = props.joint.id;

    const clearErrors = ()  => {
        setErrorX(false);
        setErrorY(false);
        setErrorZ(false);
    }

    const resetState = () => {
        setDisplay(props.joint.display);
        setColour(props.joint.colour);
        setX(props.joint.x);
        setY(props.joint.y);
        setZ(props.joint.z);
        setGlobalX(false);
        setGlobalY(false);
        setGlobalZ(false);
        clearErrors();
    }

    const handleJointDisplayChange = (e) => {
        setDisplay(e.target.checked);
    }

    const handleJointColourChange = (e) => {
        setColour(e.target.text);
    }

    const handlePositionChange = (e) => {
        clearErrors();
        if ("x"===e.target.name) {
            setX(e.target.value);
        } else if ("y"===e.target.name) {
            setY(e.target.value);
        } else if ("z"===e.target.name) {
            setZ(e.target.value);
        }
    }

    const handleGlobalUpdateChange = (e) => {
        if (GLOBAL_X===e.target.value) {
            setGlobalX(e.target.checked);
        } else if (GLOBAL_Y===e.target.value) {
            setGlobalY(e.target.checked);
        } else if (GLOBAL_Z===e.target.value) {
            setGlobalZ(e.target.checked);
        }
    }

    const handleUpdateClick = (e) => {
        if (!x || x=="") {
            setErrorX(true);
        } else if (!y || y=="") {
            setErrorY(true);
        } else if (!z || z=="") {
            setErrorZ(true);
        } else {
            props.updateJoint(props.frameId,
                jointId,
                display,
                colour,
                x,
                y,
                z,
                globalX,
                globalY,
                globalZ);
        }
    }

    const loadColourOptions = () => {
        return jointColours.map((col) => {
            return(<li key={col}>
                    <a className={colour == col ? "dropdown-item active" : "dropdown-item"}
                       onClick={handleJointColourChange}
                       href="#">{col}</a>
                   </li>
            );
        });
    }

    useEffect(() => {

        if (props.updateProps) {
            resetState();
            props.setUpdateProps(false);
        }

        if (props.updateJoints)  {
            resetState();
            props.setUpdateJoints(false);
        }

        if (errorX) {
            errorXRef.current.className = "bg-danger text-white";
        } else {
            errorXRef.current.className = "";
        }
        if (errorY) {
            errorYRef.current.className = "bg-danger text-white";
        } else {
            errorYRef.current.className = "";
        }
        if (errorZ) {
            errorZRef.current.className = "bg-danger text-white";
        } else {
            errorZRef.current.className = "";
        }
    });

    return(
        <tr key={jointId}>
            <td>{jointId}</td>
            <td>
                <button className="btn btn-success" onClick={handleUpdateClick}>Update</button>
            </td>
            <td>
                <input className="form-check-input"
                       type="checkbox"
                       checked={display}
                       onChange={handleJointDisplayChange}/>
            </td>
            <td>
                <div className="dropdown">
                    <button className="btn btn-secondary btn-sm dropdown-toggle" type="button" id="jointColour" data-bs-toggle="dropdown" aria-expanded="false">
                        Joint Colour
                    </button>
                    <ul className="dropdown-menu" aria-labelledby="jointColour">
                        {loadColourOptions()}
                    </ul>
                </div>
            </td>
            <td>
                <input name="x" ref={errorXRef} type="number" value={x} onChange={handlePositionChange} />
            </td>
            <td>
                <input className="form-check-input"
                       type="checkbox"
                       value={GLOBAL_X}
                       checked={globalX}
                       onChange={handleGlobalUpdateChange}/>
            </td>
            <td>
                <input name="y" ref={errorYRef} type="number" value={y} onChange={handlePositionChange} />
            </td>
            <td>
                <input className="form-check-input"
                       type="checkbox"
                       value={GLOBAL_Y}
                       checked={globalY}
                       onChange={handleGlobalUpdateChange}/>
            </td>
            <td>
                <input name="z" ref={errorZRef} type="number" value={z} onChange={handlePositionChange} />
            </td>
            <td>
                <input className="form-check-input"
                       type="checkbox"
                       value={GLOBAL_Z}
                       checked={globalZ}
                       onChange={handleGlobalUpdateChange}/>
            </td>
        </tr>
    );
}

export default Joint;