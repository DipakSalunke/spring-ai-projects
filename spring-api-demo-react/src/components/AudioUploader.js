import React, { useState } from "react";
import axios from "axios";
function AudioUploader() {
  const [file, setFile] = useState(null);
  const [chatResponse, setChatResponse] = useState("");
  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
  };
  const handleUpload = async () => {
    const formData = new FormData();
    formData.append("file", file);
    try {
      const response = await axios.post(
        `http://localhost:8080/api/transcribe`,
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        },
      );
      const data = response.data;
      setChatResponse(data);
      console.log(data);
    } catch (error) {
      console.log("Error generating audio transcirber response : ", error);
    }
  };
  return (
    <div className="container">
      <h2>Audio to Text Transcriber</h2>
      <div className="file-input">
        <input
          type="file"
          accept="audio/*"
          onChange={handleFileChange}
          placeholder="Select a audio file for transcription"
        />
        <button className="upload-button" onClick={handleUpload}>
          Upload & Transcribe
        </button>
        <div className="transcription-result">
          <h4>Transcription Result</h4>
          <p>{chatResponse}</p>
        </div>
      </div>
    </div>
  );
}
export default AudioUploader;
