'use strict';

import React from 'react';
import { useEffect } from 'react';
import { useLayoutEffect } from 'react';

const Viewer = (props) => {

    const [viewerSize, setViewerSize] = React.useState([500, 500]);

    let canvasRef = React.useRef();
    let viewerRef = React.useRef();

    useLayoutEffect(() => {
        function updateBounds() {
            setViewerSize([viewerRef.current.clientWidth, viewerRef.current.clientHeight]);
        }
        window.addEventListener('resize', updateBounds);
        updateBounds();
        return () => window.removeEventListener('resize', updateBounds);
    }, []);

    useEffect(() => {

        let canvas = canvasRef.current;
        let context = canvas.getContext('2d');
        context.font = '12px serif';
        let width = canvas.width;
        let height = canvas.height;

        let frame = props.scene.frames[props.currentFrame];
        let bounds = props.scene.bounds;
        let sceneOffset = {x: bounds.minX, y: bounds.minY, z: bounds.minZ};
        let screenOffset = {x: 0, y: 0};
        let scale = props.playbackParameters.scale;

        const render = () => {
            //context.clearRect(0, 0, width, height);
            context.fillStyle = "black";
            context.fillRect(0, 0, width, height);

            const renderFrame = (f, showStart) => {
                if (f) {
                    if (props.offset.jointId) {
                        let offset = f.joints.filter(joint => joint.id == props.offset.jointId)[0];
                        sceneOffset = { x: props.offset.constrainX && offset ? offset.x : 0.0,
                            y: props.offset.constrainY && offset ? offset.y : 0.0,
                            z: props.offset.constrainZ && offset ? offset.z : 0.0};
                        screenOffset = {x: width / 2, y: height / 2};
                    } else if (props.offset.x && props.offset.x != '' &&
                        props.offset.y && props.offset.y != '' &&
                        props.offset.z && props.offset.z != '') {
                        sceneOffset = {x: props.offset.constrainX ? props.offset.x : 0.0,
                            y: props.offset.constrainY ? props.offset.y : 0.0,
                            z: props.offset.constrainZ ? props.offset.z : 0.0};
                        screenOffset = {x: width / 2, y: height / 2};
                    }

                    for (var i = 0; i < f.joints.length; i++) {
                        let joint = f.joints[i];
                        if (joint.display) {
                            let x = (joint.x - sceneOffset.x) * scale;
                            let y = (joint.y - sceneOffset.y) * scale;
                            let z = (joint.z - sceneOffset.z) * scale;

                            //view projection
                            let xPos = x + screenOffset.x;
                            let yPos = height - y - screenOffset.y;
                            if (props.playbackParameters.view == "YZ") {
                                xPos = z + screenOffset.x;
                            } else if (props.playbackParameters.view == "XZ") {
                                yPos = height - z - screenOffset.y;
                            }

                            if (showStart) {
                                context.fillStyle = "grey";
                            } else {
                                context.fillStyle = joint.colour;
                            }
                            context.beginPath();
                            context.arc(xPos, yPos, 4, 0, 2 * Math.PI);
                            context.fill();
                            context.fillText("J:" + joint.id, xPos + 10, yPos + 10);
                        }
                    }
                }
            };

            renderFrame(frame, false);
            if (props.playbackParameters.showLoopStart) {
                renderFrame(props.scene.frames[props.playbackParameters.startFrame], true);
            }

            if (props.showStats) {
                const xText = 150;
                context.fillStyle = "white";
                context.fillText("File: " + props.scene.filename, width - xText, 20);
                context.fillText("Current Frame: " + props.currentFrame, width - xText, 40);
                context.fillText("Start Frame: " + props.playbackParameters.startFrame, width - xText, 60);
                context.fillText("End Frame: " + props.playbackParameters.endFrame, width - xText, 80);
                context.fillText("Total Frames: " + props.totalFrames, width - xText, 100);
                context.fillText("Scale: " + props.playbackParameters.scale, width - xText, 120);
                context.fillText("View: " + props.playbackParameters.view, width - xText, 140);
            }
        };

        render();
    });

    return (
        <div ref={viewerRef} className="container-fluid h-100 p-0 bg-dark">
             <canvas ref={canvasRef} width={viewerSize[0]} height={viewerSize[1]} />
        </div>
    );
};

export default Viewer;