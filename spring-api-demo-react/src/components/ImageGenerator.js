import React, { useState } from "react";

function ImageGenerator() {
  const [prompt, setPrompt] = useState("");
  const [imageUrls, setImageUrls] = useState([]);
  const generateImage = async () => {
    try {
      const response = await fetch(
        `http://localhost:8080/generate-image-options?prompt=${prompt}&quality=hd&n=2&widhth=512&height=512`,
      );
      const urls = await response.json();
      setImageUrls(urls);
    } catch (error) {
      console.log("Error geenrating iamge : ", error);
    }
  };
  return (
    <div className="tab-content">
      <h2>Generate Image</h2>
      <input
        type="text"
        value={prompt}
        onChange={(e) => setPrompt(e.target.value)}
        placeholder="Enter prompt for iamge"
      />
      <button onClick={generateImage}>Generate Image</button>

      <div className="image-grid">
        {imageUrls.map((url, index) => (
          <img key={index} src={url} alt={`Generated ${index}`} />
        ))}
        {[...Array(4 - imageUrls.length)].map((_, index) => (
          <div key={index + imageUrls.length} className="empty-image-slot">
            {" "}
          </div>
        ))}
      </div>
    </div>
  );
}
export default ImageGenerator;
