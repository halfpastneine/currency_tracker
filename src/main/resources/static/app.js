const qs = id => document.getElementById(id);

async function request(url, options = {}) {
    const response = await fetch(url, {
        headers: { "Content-Type": "application/json", ...(options.headers || {}) },
        ...options
    });

    if (!response.ok) {
        let message = `HTTP ${response.status}`;
        try {
            const body = await response.json();
            message = body.detail || body.message || message;
        } catch (_) {}
        throw new Error(message);
    }

    if (response.status === 204) return null;
    return response.json();
}
async function loadPrice() {
    const type = qs("priceType").value;
    const base = qs("priceSymbol").value.trim();
    const quote = qs("priceQuote").value.trim();

    qs("priceResult").textContent = "Loading...";
    try {
        const data = await request(
            `/api/price?type=${encodeURIComponent(type)}&base=${encodeURIComponent(base)}&quote=${encodeURIComponent(quote)}`
        );
        qs("priceResult").textContent =
            `${data.base}/${data.quote}: ${data.currentPrice} (${data.apiName})`;
    } catch (e) {
        qs("priceResult").innerHTML = `<span class="error">${escapeHtml(e.message)}</span>`;
    }
}

qs("alertForm").addEventListener("submit", async event => {
    event.preventDefault();
    const body = {
        type: qs("alertType").value,
        base: qs("alertSymbol").value.trim(),
        quote: qs("alertQuote").value.trim(),
        targetPrice: qs("alertTarget").value,
        up: qs("alertDirection").value === "UP",
        email: qs("alertEmail").value.trim()
    };

    try {
            const data = await request(
                `/api/price?type=${encodeURIComponent(body.type)}&base=${encodeURIComponent(body.base)}&quote=${encodeURIComponent(body.quote)}`
            );
        } catch (e) {
            qs("formMessage").innerHTML = `<span class="error">${escapeHtml(e.message)}</span>`;
            return;
        }


    try {
        await request("/api/alerts", { method: "POST", body: JSON.stringify(body) });
        qs("formMessage").textContent = "Alert created.";
    } catch (e) {
        qs("formMessage").innerHTML = `<span class="error">${escapeHtml(e.message)}</span>`;
    }
});



function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}
