import mergeHeaders from "./mergeheader";
import BASE_URL from "../components/config/Config.jsx";
async function fetcher(input, options) {
  // your headers
  const token = localStorage.getItem("token");
  console.log("Token from localStorage:", token ? token.substring(0, 20) + "..." : "NO TOKEN");
  const defaultHeaders = token ? { Authorization: `Bearer ${token}` } : {};
  console.log("Default headers:", defaultHeaders);
  // merge them with the headers of the options
  const headers = mergeHeaders(defaultHeaders, options?.headers);
  console.log("Merged headers:", headers);
  // merge the options with the headers
  const fetchOptions = { ...options, headers };
  // fetch
  try {
    input = input.replace(/^\//, '');
    const url = BASE_URL + input;
    console.log("Fetching:", url, "with options:", fetchOptions);
    const response = await fetch(`${BASE_URL}${input}`, fetchOptions);
    console.log("Response status:", response.status);
    return response;
  } catch(e) {
    throw e;
  }

}
export default fetcher;
