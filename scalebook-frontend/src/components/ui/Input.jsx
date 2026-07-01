// src/components/ui/Input.jsx

export default function Input({ label, name, type = 'text', value, onChange, error, placeholder = '' }) {
    return (
        <div className="flex flex-col gap-1">
            {label && (
                <label htmlFor={name} className="text-sm font-medium text-gray-700">
                    {label}
                </label>
            )}
            <input
                id={name}
                name={name}
                type={type}
                value={value}
                onChange={onChange}
                placeholder={placeholder}
                className={`border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500
          ${error ? 'border-red-500' : 'border-gray-300'}`}
            />
            {error && <span className="text-xs text-red-500">{error}</span>}
        </div>
    )
}