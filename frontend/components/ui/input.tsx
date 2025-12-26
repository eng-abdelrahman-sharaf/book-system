import { cn } from "@/lib/utils";

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
    label?: string;
    icon?: React.ComponentType<{ className?: string }>;
    containerClassName?: string;
}

export const Input = ({ label, icon: Icon, containerClassName,className,...props }: InputProps) => {
    return (
        <div className={containerClassName}>
            {label && (
                <label
                    htmlFor={props.id}
                    className="block text-sm font-medium text-gray-700 mb-2">
                    {label}
                </label>
            )}
            <div className="relative">
                {Icon && (
                    <Icon className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
                )}
                <input
                    {...props}
                    className={cn("w-full pl-11 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-amber-500 focus:border-transparent outline-none transition",className)}
                />
            </div>
        </div>
    );
};
