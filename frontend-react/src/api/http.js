const DEFAULT_HEADERS = {
  "Content-Type": "application/json",
};

async function handleResponse(response) {
  if (!response.ok) {
    let message = "Request failed";
    try {
      const text = await response.text();
      if (text) message = text;
    } catch {
      // If parsing fails, fall back to default message.
    }
    throw new Error(message);
  }

  if (response.status === 204) {
    return null;
  }

  const contentType = response.headers.get("content-type") || "";
  if (contentType.includes("application/json")) {
    return response.json();
  }

  return response.text();
}

export async function get(url, token) {
  const headers = { ...DEFAULT_HEADERS };
  if (token) headers.Authorization = `Bearer ${token}`;

  const response = await fetch(url, { headers });
  return handleResponse(response);
}

export async function post(url, body, token) {
  const headers = { ...DEFAULT_HEADERS };
  if (token) headers.Authorization = `Bearer ${token}`;

  const response = await fetch(url, {
    method: "POST",
    headers,
    body: JSON.stringify(body),
  });
  return handleResponse(response);
}
